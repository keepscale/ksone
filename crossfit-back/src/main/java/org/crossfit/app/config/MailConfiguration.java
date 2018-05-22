package org.crossfit.app.config;

import org.apache.commons.lang3.CharEncoding;
import org.crossfit.app.mail.LogMailSender;
import org.crossfit.app.mail.MailSender;
import org.crossfit.app.mail.SendGridMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class MailConfiguration {

	private final Logger log = LoggerFactory.getLogger(MailConfiguration.class);

    @Autowired
    private Environment env;
	
    @Bean
    @Description("Thymeleaf template resolver serving HTML 5 emails")
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("mails/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding(CharEncoding.UTF_8);
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
    }

	@Bean
	@Primary
	@ConditionalOnProperty("mail.sendgrid.api.key")
	public MailSender sendGridMailSender() {
    	String apiKey = env.getProperty("mail.sendgrid.api.key");
		log.debug("Configuring sendgrid mail server with api key {}", apiKey);
		return new SendGridMailSender(apiKey);
	}


	@Bean
	public MailSender logMailSender() {
		log.debug("Configuring LOG  mail server");
		return new LogMailSender();
	}
}
