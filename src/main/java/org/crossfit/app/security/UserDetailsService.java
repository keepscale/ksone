package org.crossfit.app.security;

import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Inject
    private CrossFitBoxSerivce boxService;
    
    @Inject
    private MemberRepository memberRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {

    	CrossFitBox box = boxService.findCurrentCrossFitBox();
    	
        log.debug("Authenticating {} for CrossFitBox {} ({})", login, box ==  null ? "null" : box.getName(), box ==  null ? "null" : box.getWebsite());
        String lowercaseLogin = login.toLowerCase();
        Optional<Member> userFromDatabase =  memberRepository.findOneByLogin(lowercaseLogin, box);
        
        return userFromDatabase.orElseThrow(
        		() -> new UsernameNotFoundException("User " + lowercaseLogin + " for " + box.getName() + "  was not found in the database"));
    }
}
