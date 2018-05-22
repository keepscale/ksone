package org.crossfit.app.security;

import org.crossfit.app.config.Constants;
import org.crossfit.app.domain.Member;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        Member member = SecurityUtils.getCurrentMember();
        return (member != null ? member.getLogin() : Constants.SYSTEM_ACCOUNT);
    }
}
