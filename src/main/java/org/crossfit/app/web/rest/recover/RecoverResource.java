package org.crossfit.app.web.rest.recover;

import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/recover")
public class RecoverResource {

    private final Logger log = LoggerFactory.getLogger(RecoverResource.class);

    @Inject
    private MemberService memberService;
    @Inject
    private MemberRepository memberRepository;
    @Inject
    private CrossFitBoxSerivce boxService;


    /**
     * PUT  /account -> update the current user information.
     */
    @RequestMapping(value = "/password",
            method = RequestMethod.POST)
    public ResponseEntity<Void> recoverPassword(@RequestBody String email) {
        log.debug("REST request to recover password for email " + email);

        
        CrossFitBox box = boxService.findCurrentCrossFitBox();
		Optional<Member> member = memberRepository.findOneByLogin(email, box);
		
		return member.map(m->{
			boolean accountNonLocked = m.isAccountNonLocked();
			if (accountNonLocked){
				memberService.initAccountAndSendMail(m);
				return ResponseEntity.ok().build();
			}
			return null;
		}).orElse(ResponseEntity.notFound().build());

    }
}
