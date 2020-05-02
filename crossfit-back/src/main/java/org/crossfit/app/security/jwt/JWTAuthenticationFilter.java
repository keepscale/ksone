package org.crossfit.app.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
 
	private JwtTokenService jwtTokenService;

	public JWTAuthenticationFilter(String authenticationPath, JwtTokenService jwtTokenService, AuthenticationManager authenticationManager) {
		super();
		this.jwtTokenService = jwtTokenService;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(authenticationPath, "POST"));
		setAuthenticationManager(authenticationManager);
	}
	
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		Authentication actualAuth = SecurityContextHolder.getContext().getAuthentication();
		if (actualAuth instanceof JWTAuthenticationToken) {
			return actualAuth;
		}
		return super.attemptAuthentication(request, response);
	}



	@Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
		
        res.addHeader("JWT_TOKEN", jwtTokenService.getStringToken(((UserDetails) auth.getPrincipal()).getUsername()));
    
	}
}