package org.crossfit.app.service;

import java.util.Locale;

import javax.inject.Inject;

import org.crossfit.app.domain.Member;
import org.crossfit.app.mail.CrossfitMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Inject
    private CrossfitMailSender mailSender;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Async
    public void sendActivationEmail(Member member, String clearPassword) {
        log.debug("Sending activation e-mail to '{}'", member.getLogin());
        Locale locale = Locale.forLanguageTag(member.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", member);
        context.setVariable("clearPassword", clearPassword);
        context.setVariable("box", member.getBox());
        String content = templateEngine.process("activationCompte", context);
        String subject = messageSource.getMessage("email.creation.title", new Object[]{member.getBox().getName()}, locale);
        mailSender.sendEmail(member.getBox().getFromEmail(), member.getLogin(), subject, content, false, true);
    }
}
