package org.crossfit.app.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.exception.EmailAlreadyUseException;
import org.crossfit.app.repository.AuthorityRepository;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.PersistentTokenRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.util.RandomUtil;
import org.crossfit.app.web.rest.api.MemberResource.HealthIndicator;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.crossfit.app.web.rest.dto.SubscriptionDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private SubscriptionRepository subscriptionRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private CrossFitBoxSerivce boxService;
    @Inject
    private TimeService timeService;
    @Inject
    private MembershipService membershipService;
    @Inject
    private AuthorityRepository authorityRepository;
    @Inject
	private MailService mailService;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	private static boolean isOverLap(List<Subscription> subscriptions) {
		List<Subscription> sortedSubs = subscriptions.stream()
			.sorted((s1,s2)->s1.getSubscriptionStartDate().compareTo(s2.getSubscriptionStartDate()))
			.collect(Collectors.toList());
		
		for (Iterator<Subscription> it = sortedSubs.iterator(); it.hasNext();) {
			Subscription actual = it.next();
			if (it.hasNext()) {
				Subscription next = it.next();
				if (next.getSubscriptionStartDate().isBefore(actual.getSubscriptionEndDate())) {
					return true;
				}
			}
		}
		
		return false;
	}

	public List<Member> findAllWithSubscriptionEndBeforeStart() {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		Set<Subscription> subscriptions = subscriptionRepository.findAllByBoxWithMembership(box);
	
		return subscriptions.stream()
				.collect(Collectors.groupingBy(Subscription::getMember)) //member:list sousscription
				.entrySet().stream()
					.filter(e->e.getValue().stream().anyMatch(s->s.getSubscriptionEndDate().isBefore(s.getSubscriptionStartDate())))
					.map(e->e.getKey())
					.collect(Collectors.toList());	
	}


	public List<Member> findAllWithSubscriptionEndNotAtEndMonth() {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		Set<Subscription> subscriptions = subscriptionRepository.findAllByBoxWithMembership(box);
		 
		 Stream<Subscription> stream = subscriptions.stream();
		 stream = stream.filter(s->membershipService.isMembershipPaymentByMonth(s.getMembership()));
		 stream = stream.filter(s->s.getSubscriptionEndDate().getDayOfMonth() != s.getSubscriptionEndDate().dayOfMonth().withMaximumValue().getDayOfMonth());
		 
		return stream.collect(Collectors.groupingBy(Subscription::getMember)) //member:list sousscription
				.entrySet().stream()
					.map(e->e.getKey())
					.collect(Collectors.toList());	
	}
	
	public List<Member> findAllWithDoubleSubscription(HealthIndicator health) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		Set<Subscription> subscriptions = subscriptionRepository.findAllByBoxWithMembership(box);
	
		 
//				.filter(s->s.getMember().getId()==1514)
				
		 
		 Stream<Subscription> stream = subscriptions.stream();
		 if (health == HealthIndicator.SUBSCRIPTIONS_OVERLAP){
			 stream = stream.filter(s->membershipService.isMembershipPaymentByMonth(s.getMembership()));
		 }
		 else  if (health == HealthIndicator.SUBSCRIPTIONS_OVERLAP_NON_RECURRENT){
			 stream = stream.filter(s->!membershipService.isMembershipPaymentByMonth(s.getMembership()));
		 }
		 
		return stream.collect(Collectors.groupingBy(Subscription::getMember)) //member:list sousscription
				.entrySet().stream()
					.filter(e->isOverLap(e.getValue()))
					.map(e->e.getKey())
					.collect(Collectors.toList());	
	}
	
	public List<Member> findAllMemberWithNoActiveSubscriptionAtNow(){
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		HashSet<Long> membershipsIds = new HashSet<>();
		membershipsIds.add(-1L);
		return findAllMemberWithNoActiveSubscriptionAtDate(timeService.nowAsLocalDate(box), membershipsIds, true);
	}

	
	public List<Member> findAllMemberWithNoActiveSubscriptionAtDate(LocalDate date, Set<Long> membershipsIds, boolean includeAllMemberships){
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		return memberRepository.findAllMemberWithNoSubscriptionAtDate(box, date, membershipsIds, includeAllMemberships);
	}

	public List<Member> findAllMemberWithActiveSubscriptionAtDate(LocalDate date, Set<Long> membershipsIds, boolean includeAllMemberships) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		return memberRepository.findAllMemberWithSubscriptionAtDate(box, date, membershipsIds, includeAllMemberships);
	}
	
	public List<Member> findAllMemberWithNoCard(){
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		return memberRepository.findAllMemberWithNoCard(box);
	}


	public List<Member> findAllMemberWithNoAddress() {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		return memberRepository.findAllMemberWithNoAddress(box);
	}
  
	
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
			member.setUuid(UUID.randomUUID().toString());
			member.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		}
		else{
			
			member = memberRepository.findOne(memberdto.getId());
		}
	
		member.getAuthorities().clear();
		member.getAuthorities().addAll(authorityRepository.findAllById(memberdto.getRoles()));

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
		member.setCardUuid(StringUtils.isBlank(memberdto.getCardUuid()) ? null : memberdto.getCardUuid());
		member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		member.setLastModifiedDate(DateTime.now(DateTimeZone.UTC));
		member.setGivenMedicalCertificate(memberdto.isGivenMedicalCertificate());
		member.setMedicalCertificateDate(memberdto.getMedicalCertificateDate());
		member.setComments(memberdto.getComments());
		member.setNumber(memberdto.getNumber());
		member.setBox(currentCrossFitBox);

		//L'email a changé ? on repasse par une validation d'email
		if (!memberdto.getEmail().equals(member.getLogin())){
			member.setLogin(memberdto.getEmail().toLowerCase());
			initAccountAndSendMail(member);
		}
		//Supprime les subscription qui ne sont plus dans le dto
		member.getSubscriptions().removeIf(sub->memberdto.getSubscriptions().stream().noneMatch(dto->sub.getId().equals(dto.getId())));
		
		
		for (SubscriptionDTO dto : memberdto.getSubscriptions()) {
			Subscription s;
			
			if (dto.getId() == null) {
				s = new Subscription();
				s.setMember(member);
				member.getSubscriptions().add(s);
			}
			else {
				s = member.getSubscriptions().stream().filter(sub->sub.getId().equals(dto.getId())).findFirst()
				.orElseThrow(()->new IllegalStateException("La souscription " + dto.getId() + " n'appartient pas à l'utilisateur " + memberdto.getId()));
			}
        	
        	
        	s.setMembership(dto.getMembership());
        	s.setSubscriptionStartDate(dto.getSubscriptionStartDate());
        	s.setSubscriptionEndDate(dto.getSubscriptionEndDate());
        	s.setPaymentMethod(dto.getPaymentMethod());
        	
        	s.setDirectDebitAfterDate(dto.getDirectDebitAfterDate());
        	s.setDirectDebitAtDayOfMonth(dto.getDirectDebitAtDayOfMonth());
        	s.setDirectDebitFirstPaymentTaxIncl(dto.getDirectDebitFirstPaymentTaxIncl());
        	s.setDirectDebitIban(dto.getDirectDebitIban());
        	s.setDirectDebitBic(dto.getDirectDebitBic());        	
        	
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
	
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails)
			member.setLastModifiedBy(((UserDetails) authentication.getPrincipal()).getUsername());
		else
			member.setLastModifiedBy("system");
		
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
   
   public boolean isValidPassword(String password){
       return Optional.of(SecurityUtils.getCurrentMember()).map(u-> {
           return passwordEncoder.matches(password, u.getPassword());
       }).orElse(false);
   }


	public void deleteMember(Long id) {
		Member memberToDelete = memberRepository.findOne(id);
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		if (memberToDelete.getBox().equals(currentCrossFitBox)){
			bookingRepository.deleteAllByMember(memberToDelete);
			memberRepository.delete(memberToDelete);
		}
	}


	public void updateAccount(MemberDTO dto) {
		
		Member online = SecurityUtils.getCurrentMember();
		
		if(online == null){
			return;
		}

		online.setTitle(dto.getTitle());
		online.setFirstName(dto.getFirstName());
		online.setLastName(dto.getLastName());
		online.setAddress(dto.getAddress());
		online.setZipCode(dto.getZipCode());
		online.setCity(dto.getCity());
		online.setTelephonNumber(dto.getTelephonNumber());
		
		memberRepository.save(online);
	}


	public Optional<Member> findMemberByUuid(String uuid) {
		return memberRepository.findOneByUuid(uuid);
	}

	public int updateInMass(Collection<Member> membersToUpdate) {
		int count = 0;
    	for (Member memberOfCSV : membersToUpdate) {
			Optional<Member> findById = memberRepository.findById(memberOfCSV.getId());
			if(findById.isPresent()) {
				count++;
				Member memberDB = findById.get();
				memberDB.setNumber(memberOfCSV.getNumber());
				memberDB.setGivenMedicalCertificate(memberOfCSV.hasGivenMedicalCertificate());
				memberDB.setMedicalCertificateDate(memberOfCSV.getMedicalCertificateDate());
				memberRepository.save(memberDB);
			}
		}
    	return count;
	}
	
}
