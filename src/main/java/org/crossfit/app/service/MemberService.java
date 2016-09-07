package org.crossfit.app.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.exception.EmailAlreadyUseException;
import org.crossfit.app.repository.AuthorityRepository;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.PersistentTokenRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.util.RandomUtil;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class MemberService {

    private final Logger log = LoggerFactory.getLogger(MemberService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private CrossFitBoxSerivce boxService;
    @Inject
    private AuthorityRepository authorityRepository;
    @Inject
	private MailService mailService;
	
	@Autowired
	private BookingRepository bookingRepository;

  
    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = new LocalDate();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).stream().forEach(token ->{
            log.debug("Deleting token {}", token.getSeries());
            Member user = token.getMember();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }
    
    
    public Member doSave(MemberDTO memberdto) throws EmailAlreadyUseException {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
    	Member member;
		if (memberdto.getId() == null){
			
			member = new Member();
			member.setAuthorities(new HashSet<Authority>(Arrays.asList(authorityRepository.findOne(AuthoritiesConstants.USER))));
			member.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		}
		else{
			
			member = memberRepository.findOne(memberdto.getId());
		}

		Optional<Member> findOneByLogin = memberRepository.findOneByLogin(memberdto.getEmail(), currentCrossFitBox);
		
		if (findOneByLogin.isPresent() && !findOneByLogin.equals(Optional.of(member))){
			throw new EmailAlreadyUseException(memberdto.getEmail());
		}
		
		member.setTitle(memberdto.getTitle());
		member.setFirstName(memberdto.getFirstName());
		member.setLastName(memberdto.getLastName());
		member.setNickName(memberdto.getNickName());
		member.setAddress(memberdto.getAddress());
		member.setZipCode(memberdto.getZipCode());
		member.setCity(memberdto.getCity());
		member.setLangKey(memberdto.getLangKey());
		member.setTelephonNumber(memberdto.getTelephonNumber());
		member.setCardUuid(memberdto.getCardUuid());
		member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		member.setLastModifiedDate(DateTime.now(DateTimeZone.UTC));
		member.setBox(currentCrossFitBox);

		//L'email a changé ? on repasse par une validation d'email
		if (!memberdto.getEmail().equals(member.getLogin())){
			member.setLogin(memberdto.getEmail().toLowerCase());
			initAccountAndSendMail(member);
		}
		member.getSubscriptions().clear();
		for (Subscription s : memberdto.getSubscriptions()) {
        	s.setMember(member);
			member.getSubscriptions().add(s);
		}
		member = memberRepository.save(member);
		return member;
	}

	public void initAccountAndSendMail(Member member) {
		String generatePassword = RandomUtil.generatePassword();
		member.setLogin(member.getLogin().toLowerCase());
		member.setPassword(passwordEncoder.encode(generatePassword));
		member.setEnabled(false);
		member.setLocked(false);
	
		member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		member.setLastModifiedDate(DateTime.now(DateTimeZone.UTC));
	
		mailService.sendActivationEmail(member, generatePassword);
		memberRepository.save(member);
	}
	


	public void lockUser(Member member) {
		member.setEnabled(false);
		member.setLocked(true);
	
		member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		member.setLastModifiedDate(DateTime.now(DateTimeZone.UTC));

		memberRepository.save(member);
	}
	
   public void changePassword(String password) {
	   Optional.of(SecurityUtils.getCurrentMember()).ifPresent(u-> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            memberRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }
   


	public void deleteMember(Long id) {
		Member memberToDelete = memberRepository.findOne(id);
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		if (memberToDelete.getBox().equals(currentCrossFitBox)){
			bookingRepository.deleteAllByMember(memberToDelete);
			memberRepository.delete(memberToDelete);
		}
	}


}
