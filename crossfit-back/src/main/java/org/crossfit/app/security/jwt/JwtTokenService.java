package org.crossfit.app.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenService {

    private static final long DEFAULT_EXPIRATION_TIME = 10 * 24 * 3600; //30 jours

	@Value("${security.jwt.expiration-time:" + DEFAULT_EXPIRATION_TIME+"}")
	private long jwtExpirationTime;
	
	@Value("${security.jwt.secret-key}")
	private String jwtSecretKey;
	
	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
	
	
	public String getStringToken(String subject) {
        long exp = System.currentTimeMillis() + (jwtExpirationTime*1000);
        
		String token = Jwts.builder()
        .setSubject(subject)
        .setExpiration(new Date(exp))
        .signWith(signatureAlgorithm, jwtSecretKey)
        .compact();
        
        return token;
	}
	
	public JWTAuthenticationToken getAuthenticationToken(String token) {
		try {
			Jws<Claims> jwt = Jwts.parser()
				.setSigningKey(jwtSecretKey)
				.parseClaimsJws(token);
			
			return new JWTAuthenticationToken(token, jwt.getBody().getSubject());
		} catch (JwtException e) {
			throw new BadCredentialsException(e.getMessage(), e);
		}
	}
}
