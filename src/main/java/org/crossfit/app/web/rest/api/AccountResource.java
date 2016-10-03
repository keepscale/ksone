package org.crossfit.app.web.rest.api;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.crossfit.app.web.rest.dto.SubscriptionDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private MemberService memberService;
    @Inject
    private TimeService timeService;
    @Inject
    private CrossFitBoxSerivce boxService;


    /**
     * GET  /authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }


    /**
     * PUT  /account -> update the current user information.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDTO> update(@Valid @RequestBody MemberDTO dto) {
        log.debug("REST request to update account");

    	memberService.updateAccount(dto);
        
        return getAccount();
    }
    
    
    /**
     * GET  /account -> get the current user.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDTO> getAccount() {
    	
    	LocalDate now = timeService.nowAsLocalDate(boxService.findCurrentCrossFitBox());
    	
        return Optional.ofNullable(SecurityUtils.getCurrentMember())
            .map(member -> {
            	MemberDTO dto = MemberDTO.MAPPER.apply(member);

        		List<String> roles = member.getAuthorities().stream().map(Authority::getName)
                 .collect(Collectors.toList());
        		dto.setRoles(roles);

        		for (Subscription subscription : new HashSet<>(member.getSubscriptions())) {
        			
        			LocalDate startAt = subscription.getSubscriptionStartDate();
					LocalDate endAt = subscription.getSubscriptionEndDate();
					if (now.equals(startAt) || now.equals(endAt) || (now.isAfter(startAt) && now.isBefore(endAt))){

            			Subscription s = new Subscription();
            			s.setSubscriptionStartDate(subscription.getSubscriptionStartDate());
            			s.setSubscriptionEndDate(subscription.getSubscriptionEndDate());
            			
            			Membership m = new Membership();
            			m.setName(subscription.getMembership().getName());
            			s.setMembership(m);
            			
            			dto.getSubscriptions().add(SubscriptionDTO.fullMapper.apply(s));
            			
            			
            			dto.getRules().addAll(subscription.getMembership().getMembershipRules());
        			}
        			
				}
        		
                return new ResponseEntity<>(dto,HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public static class PasswordDTO{
    	String actualPassword;
		String newPassword;
		
		public String getActualPassword() {
			return actualPassword;
		}
		public void setActualPassword(String actualPassword) {
			this.actualPassword = actualPassword;
		}
		public String getNewPassword() {
			return newPassword;
		}
		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}
		
		
    }

    /**
     * POST  /change_password -> changes the current user's password
     */
    @RequestMapping(value = "/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePassword(@RequestBody PasswordDTO pwd) {
        if (!checkPasswordLength(pwd.getNewPassword())) {
            return new ResponseEntity<>("Nouveau mot de passe trop court", HttpStatus.BAD_REQUEST);
        }
        if (!memberService.isValidPassword(pwd.getActualPassword())){
            return new ResponseEntity<>("Mot de passe actuel incorrect", HttpStatus.BAD_REQUEST);
        }
        memberService.changePassword(pwd.getNewPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private boolean checkPasswordLength(String password) {
      return (!StringUtils.isEmpty(password) && password.length() >= MemberDTO.PASSWORD_MIN_LENGTH && password.length() <= MemberDTO.PASSWORD_MAX_LENGTH);
    }

}
