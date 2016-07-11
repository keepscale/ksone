package org.crossfit.app.service;

import java.util.Arrays;
import java.util.HashSet;

import javax.inject.Inject;

import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.AuthorityRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.PersistentTokenRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class MemberService {

    private final Logger log = LoggerFactory.getLogger(MemberService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private MemberRepository userRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    public Member createUserInformation(String login, String password, String firstName, String lastName, String langKey) {

    	Member newMember = new Member();
        String encryptedPassword = passwordEncoder.encode(password);
        newMember.setLogin(login);
        // new user gets initially a generated password
        newMember.setPassword(encryptedPassword);
        newMember.setFirstName(firstName);
        newMember.setLastName(lastName);
        newMember.setLangKey(langKey);
        
        newMember.setAuthorities(new HashSet<Authority>(Arrays.asList(authorityRepository.findOne(AuthoritiesConstants.USER))));
        userRepository.save(newMember);
        log.debug("Created Information for Member: {}", newMember);
        return newMember;
    }

    public void updateUserInformation(String firstName, String lastName, String langKey, String telephonNumber) {
        Member currentMember = SecurityUtils.getCurrentMember();
		userRepository.findOneByLogin(currentMember.getLogin(), currentMember.getBox()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setLangKey(langKey);
            u.setTelephonNumber(telephonNumber);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void changePassword(String password) {        
    	Member currentMember = SecurityUtils.getCurrentMember();
    	userRepository.findOneByLogin(currentMember.getLogin(), currentMember.getBox()).ifPresent(u-> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = new LocalDate();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).stream().forEach(token ->{
            log.debug("Deleting token {}", token.getSeries());
            Member user = token.getMember();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }
}
