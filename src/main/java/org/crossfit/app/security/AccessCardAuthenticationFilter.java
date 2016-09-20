package org.crossfit.app.security;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class AccessCardAuthenticationFilter extends OncePerRequestFilter {

	
	private String tokenValue;

	public AccessCardAuthenticationFilter(String tokenValue) {
		super();
		this.tokenValue = tokenValue;
	}

	@Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
		
        String xAccessToken = request.getHeader("X-Access-Token");
        
        if (StringUtils.isNotEmpty(xAccessToken)){
        	
        	 if(!StringUtils.equals(xAccessToken, tokenValue)){
                 throw new SecurityException();
             }                            
             
             
             Authentication auth = new AccessCardAuthentication();
             auth.setAuthenticated(true);
             SecurityContextHolder.getContext().setAuthentication(auth);           
        }
        
        filterChain.doFilter(request, response);
    }
	
	

    public class AccessCardAuthentication extends AbstractAuthenticationToken {

    	private Principal principal;
    	
		public AccessCardAuthentication() {
			super(Arrays.asList(new SimpleGrantedAuthority(AuthoritiesConstants.ACCESS_CARD)));
			principal = new Principal() {
				
				@Override
				public String getName() {
					return "access-card";
				}
			};
		}

		@Override
		public Object getCredentials() {
			return "";
		}

		@Override
		public Object getPrincipal() {
			return principal;
		}



	}


}