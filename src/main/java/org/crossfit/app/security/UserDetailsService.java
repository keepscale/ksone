package org.crossfit.app.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.User;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.UserRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private UserRepository userRepository;

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
        Optional<User> userFromDatabase =  userRepository.findOneByLogin(lowercaseLogin);
        
        return userFromDatabase.map(user -> {
            if (!user.getActivated()) {
                throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
            }
            List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                    .collect(Collectors.toList());
            
            if (box != null && box.getAdministrators().contains(user)){
            	grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.MANAGER));
            }
            
            boolean isSuperAdmin = grantedAuthorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
            
            if(box != null && !isSuperAdmin){
            	// On charge le membre pour voir si il a accès à la box
            	Member membre = memberRepository.findOneByLogin(lowercaseLogin, box);
            	if(membre == null){
            		throw new InternalAuthenticationServiceException("L'utilisateur " + lowercaseLogin + " n'a pas le droit d'accéder à la box " + box.getName());
            	}
            }
            
            if (box == null && !isSuperAdmin){
            	throw new InternalAuthenticationServiceException(
            			"Aucune box n'est configuree et l'utilisateur " + lowercaseLogin + " na pas le role " + AuthoritiesConstants.ADMIN);
            }
            
            
            
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(lowercaseLogin,
                    user.getPassword(),
                    grantedAuthorities);
            

            log.debug("User Authenticated {}", userDetails); 
            
            return userDetails;
        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }
}
