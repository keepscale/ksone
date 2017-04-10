package org.crossfit.app.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String DIRECTOR = "ROLE_DIRECTOR";
    
    public static final String MANAGER = "ROLE_MANAGER";

    public static final String USER = "ROLE_USER";
    
    public static final String RENTER = "ROLE_RENTER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

	public static final String ACCESS_CARD = "ROLE_ACCESS_CARD";
}
