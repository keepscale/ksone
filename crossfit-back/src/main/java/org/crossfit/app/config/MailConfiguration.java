package org.crossfit.app.config;

import org.crossfit.app.mail.LogMailSender;
import org.crossfit.app.mail.MailSender;
import org.crossfit.app.mail.SMTPMailSender;
import org.crossfit.app.mail.SendGridMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class MailConfiguration {

	private final Logger log = LoggerFactory.getLogger(MailConfiguration.class);

    @Autowired
    private Environment env;

	@Bean
	@ConditionalOnProperty("mail.sendgrid.api.key")
	public MailSender sendGridMailSender() {
    	String apiKey = env.getProperty("mail.sendgrid.api.key");
		log.debug("Configuring sendgrid mail server with api key {}", apiKey);
		return new SendGridMailSender(apiKey);
	}
	
	@Bean
	@Primary
	@ConditionalOnProperty("spring.mail.host")
	public MailSender getSMTPMailSender() {
		log.debug("Configuring SMTP  mail server");
	    return new SMTPMailSender();
	}

	@Bean
	public MailSender logMailSender() {
		log.debug("Configuring LOG  mail server");
		return new LogMailSender();
	}
}
