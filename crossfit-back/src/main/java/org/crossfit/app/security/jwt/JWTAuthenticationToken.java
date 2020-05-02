package org.crossfit.app.security.jwt;

import java.util.ArrayList;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class JWTAuthenticationToken extends AbstractAuthenticationToken{

	private static final long serialVersionUID = 1L;
	private final String token;
	private final String subject;
	private final UserDetails user;
	
	public JWTAuthenticationToken(String token, String subject) {
		super(new ArrayList<>());
		this.token = token;
		this.subject = subject;
		this.user = null;
		setAuthenticated(false);
	}
	
	public JWTAuthenticationToken(String token, UserDetails user) {
		super(user.getAuthorities());
		this.token = token;
		this.subject = user.getUsername();
		this.user = user;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return user == null ? subject : user;
	}

}
