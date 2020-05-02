package org.crossfit.app.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import groovy.util.logging.Slf4j;

@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final String CHALLENGE_NAME = "Bearer";
	private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	
	private JwtTokenService jwtTokenService;
	private UserDetailsService userDetailsService;
	private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

	public JWTAuthorizationFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
		super();
		this.jwtTokenService = jwtTokenService;
		this.userDetailsService = userDetailsService;
	}
	
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(AUTHORIZATION_HEADER_NAME);

        if (header == null || !header.startsWith(CHALLENGE_NAME)) {
            chain.doFilter(req, res);
            return;
        }

        try {
			JWTAuthenticationToken attemptAuthentication = jwtTokenService.getAuthenticationToken(
					header.substring(CHALLENGE_NAME.length()+1));
			UserDetails user = userDetailsService.loadUserByUsername(attemptAuthentication.getName());
			if (user != null) {
				if (!user.isAccountNonLocked()) {
					throw new LockedException("Account locked");
				}
				
			    JWTAuthenticationToken authentication = new JWTAuthenticationToken((String)attemptAuthentication.getCredentials(), user);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			else {
				throw new UsernameNotFoundException(attemptAuthentication.getName() + " not found");
			}
			
		} catch (AuthenticationException e) {
			logger.warn("Header " + header + " invalid.", e);
			failureHandler.onAuthenticationFailure(req, res, e);
			return;
		}

        chain.doFilter(req, res);
		
    }

}