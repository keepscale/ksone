package org.crossfit.app.web.rest.manage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.AuthorityRepository;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MailService;
import org.crossfit.app.service.util.RandomUtil;
import org.crossfit.app.web.rest.api.MemberResource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/manage")
public class CrossFitBoxMemberResource extends MemberResource {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxMemberResource.class);

    @Inject
    private MemberRepository memberRepository;
    @Inject
    private BookingRepository bookingRepository;

    @Inject
    private CrossFitBoxSerivce boxService;

    @Inject
    private AuthorityRepository authorityRepository;
	
    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
	private MailService mailService;

	@Override
	protected Member doSave(Member member) {
		if (member.getId() == null){
			
			member.setAuthorities(new HashSet<Authority>(Arrays.asList(authorityRepository.findOne(AuthoritiesConstants.USER))));
			member.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			member.setCreatedDate(DateTime.now());			
			member.setBox(boxService.findCurrentCrossFitBox());
			

			initAccountAndSendMail(member);
		}
		else{
			//Les seuls champs modifiable de user, c'est le nom, le prénom & l'email
			String firstName = member.getFirstName();
			String lastName = member.getLastName();
			String telephonNumber = member.getTelephonNumber();
			String login = member.getLogin();
			
			Member actualMember = memberRepository.findOne(member.getId());
			actualMember.setFirstName(firstName);
			actualMember.setLastName(lastName);
			actualMember.setTelephonNumber(telephonNumber);
			actualMember.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			actualMember.setLastModifiedDate(DateTime.now());
			
			
			//L'email a changé ? on repasse par une validation d'email
			if (!login.equals(actualMember.getLogin())){
				actualMember.setLogin(login.toLowerCase());
				initAccountAndSendMail(member);
			}
		}

		
		return super.doSave(member);
	}

	protected void initAccountAndSendMail(Member member) {
		String generatePassword = RandomUtil.generatePassword();
		member.setLogin(member.getLogin().toLowerCase());
		member.setPassword(passwordEncoder.encode(generatePassword));
		member.setEnabled(false);
		member.setLocked(false);

		mailService.sendActivationEmail(member, generatePassword);
	}

	@Override
	protected Page<Member> doFindAll(Pageable generatePageRequest) {
		return memberRepository.findAll(boxService.findCurrentCrossFitBox(), generatePageRequest);
	}

	@Override
	protected Member doGet(Long id) {
		return memberRepository.findOne(id);
	}

	@Override
	protected void doDelete(Long id) {
		
		boxService.deleteMember(id);
	}
	
	@RequestMapping(value = "/members/{id}/resetaccount", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> reset(@PathVariable Long id) {
		log.debug("REST request to reset Member : {}", id);
		Member member = doGet(id);
		if (member != null){
			member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			member.setLastModifiedDate(DateTime.now());
		
			initAccountAndSendMail(member);

			try {
				super.doSave(member);
			} catch (Exception e) {
				log.warn("Impossible d'envoyer le mail a {}", member.getLogin());
			}
		}
		return ResponseEntity.ok().build();
	}
	@RequestMapping(value = "/members/massActivation", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> massActivation() {
		log.debug("Envoi du mail d'activation a tous les membres non actif");
		
		List<Member> allMembersNotActivated = memberRepository.findAllUserNotEnabled(boxService.findCurrentCrossFitBox());
		
		for (Member member : allMembersNotActivated) {
			
			member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			member.setLastModifiedDate(DateTime.now());
			
			initAccountAndSendMail(member);
			
			try {
				super.doSave(member);
			} catch (Exception e) {
				log.warn("Impossible d'envoyer le mail a {}", member.getLogin());
			}
		}
		return ResponseEntity.ok().build();
	}
		
}
