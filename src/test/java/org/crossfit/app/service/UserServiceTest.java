package org.crossfit.app.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.crossfit.app.Application;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.PersistentToken;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.PersistentTokenRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the UserResource REST controller.
 *
 * @see MemberService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class UserServiceTest {

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private MemberService userService;

    @Test
    public void testRemoveOldPersistentTokens() {
    	Member admin = memberRepository.findOneByLogin("admin", null).get();
        int existingCount = persistentTokenRepository.findByMember(admin).size();
        generateUserToken(admin, "1111-1111", new LocalDate());
        LocalDate now = new LocalDate();
        generateUserToken(admin, "2222-2222", now.minusDays(32));
        assertThat(persistentTokenRepository.findByMember(admin)).hasSize(existingCount + 2);
        userService.removeOldPersistentTokens();
        assertThat(persistentTokenRepository.findByMember(admin)).hasSize(existingCount + 1);
    }


    private void generateUserToken(Member user, String tokenSeries, LocalDate localDate) {
        PersistentToken token = new PersistentToken();
        token.setSeries(tokenSeries);
        token.setMember(user);
        token.setTokenValue(tokenSeries + "-data");
        token.setTokenDate(localDate);
        token.setIpAddress("127.0.0.1");
        token.setUserAgent("Test agent");
        persistentTokenRepository.saveAndFlush(token);
    }
}
