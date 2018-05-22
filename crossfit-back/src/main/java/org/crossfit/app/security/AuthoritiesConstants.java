package org.crossfit.app.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    //Super admin
    public static final String ADMIN = "ROLE_ADMIN";

    //Les stats
    public static final String DIRECTOR = "ROLE_DIRECTOR";
    
    //La facturation
    public static final String COMPTABLE = "ROLE_COMPTABLE";
    
    //Gérer le planning et les résas
    public static final String MANAGER = "ROLE_MANAGER";
    
    //Voir le planning
    public static final String COACH = "ROLE_COACH";
    
    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

	public static final String ACCESS_CARD = "ROLE_ACCESS_CARD";
}
