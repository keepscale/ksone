package org.crossfit.app.web.rest.use;

import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.AuthorityRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.MemberResource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/use")
public class CrossFitBoxCurrentMemberResource extends MemberResource {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxCurrentMemberResource.class);

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private CrossFitBoxSerivce boxService;
	
	@Override
	protected Member doSave(Member member) {
		Member oldMember = doGet(member.getId());
		
		oldMember.setLevel(member.getLevel());
		oldMember.setMembershipEndDate(member.getMembershipEndDate());
		oldMember.setMembershipStartDate(member.getMembershipStartDate());
		oldMember.setSickNote(member.getSickNote());
		oldMember.setTelephonNumber(member.getTelephonNumber());
		
		oldMember.getUser().setFirstName(member.getUser().getFirstName());
		oldMember.getUser().setLastName(member.getUser().getLastName());
		oldMember.getUser().setLangKey(member.getUser().getLangKey());
		oldMember.getUser().setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		oldMember.getUser().setLastModifiedDate(DateTime.now());
		
		Member result = memberRepository.save(oldMember);
		return result;
	}

	/**
	 * GET /members/logged -> get the current member.
	 */
	@RequestMapping(value = "/members/logged", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Member> getCurrentMember() {
		log.debug("REST request to get current Member : {}", SecurityUtils.getCurrentLogin());
		
		
		return Optional.ofNullable(doGetCurrent())
				.map(member -> new ResponseEntity<>(member, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
    
	protected Member doGetCurrent() {
		return memberRepository.findOneByLogin(SecurityUtils.getCurrentLogin(), boxService.findCurrentCrossFitBox());
	}
	
}
