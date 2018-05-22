package org.crossfit.app.config;

import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.mail.CrossfitMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

@Configuration
public class MailConfiguration implements EnvironmentAware {

	private static final String ENV_SPRING_MAIL = "mail.";
	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final String PROP_HOST = "host";
	private static final String DEFAULT_PROP_HOST = "localhost";
	private static final String PROP_PORT = "port";
	private static final String PROP_USER = "username";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_PROTO = "protocol";
	private static final String PROP_TLS = "tls";
	private static final String PROP_AUTH = "auth";
	private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
	private static final String PROP_STARTTLS = "mail.smtp.starttls.enable";
	private static final String PROP_TRANSPORT_PROTO = "mail.transport.protocol";

	private final Logger log = LoggerFactory.getLogger(MailConfiguration.class);

	private RelaxedPropertyResolver propertyResolver;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment,
				ENV_SPRING_MAIL);
	}
	
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
	public CrossfitMailSender mailSender() {
		log.debug("Configuring mail server");
		String host = propertyResolver
				.getProperty(PROP_HOST, DEFAULT_PROP_HOST);
		int port = propertyResolver.getProperty(PROP_PORT, Integer.class, 0);
		String user = propertyResolver.getProperty(PROP_USER);
		String password = propertyResolver.getProperty(PROP_PASSWORD);
		String protocol = propertyResolver.getProperty(PROP_PROTO);
		Boolean tls = propertyResolver.getProperty(PROP_TLS, Boolean.class,
				false);
		Boolean auth = propertyResolver.getProperty(PROP_AUTH, Boolean.class,
				false);

		if (StringUtils.equals(protocol, "sendgrid")) {
			SendGrid sendgrid = new SendGrid(user, password);

			return new CrossfitMailSender() {

				@Override
				@Async
				public void sendEmail(String from, String to, String subject,
						String content, boolean isMultipart, boolean isHtml) {
					try {

						SendGrid.Email email = new SendGrid.Email();

						email.addTo(to);
						email.setFrom(from);
						email.setSubject(subject);
						if (isHtml) {
							email.setHtml(content);
						} else {
							email.setText(content);
						}

						SendGrid.Response response = sendgrid.send(email);
						log.debug(
								"Sent e-mail to User '{}'. SendGrid.Response:'{}'",
								to, response.getMessage());
					} catch (SendGridException e) {
						log.warn(
								"E-mail could not be sent to user '{}' with SendGrid, exception is: {}",
								to, e.getMessage());
					}
				}
			};
		} else {

			JavaMailSenderImpl sender = new JavaMailSenderImpl();
			if (host != null && !host.isEmpty()) {
				sender.setHost(host);
			} else {
				log.warn("Warning! Your SMTP server is not configured. We will try to use one on localhost.");
				log.debug("Did you configure your SMTP settings in your application.yml?");
				sender.setHost(DEFAULT_HOST);
			}
			sender.setPort(port);
			sender.setUsername(user);
			sender.setPassword(password);

			Properties sendProperties = new Properties();
			sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
			sendProperties.setProperty(PROP_STARTTLS, tls.toString());
			sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
			sender.setJavaMailProperties(sendProperties);

			return new CrossfitMailSender() {

				@Override
				@Async
				public void sendEmail(String from, String to, String subject,
						String content, boolean isMultipart, boolean isHtml) {

					MimeMessage mimeMessage = sender.createMimeMessage();
					try {
						MimeMessageHelper message = new MimeMessageHelper(
								mimeMessage, isMultipart, CharEncoding.UTF_8);
						message.setTo(to);
						message.setFrom(from);
						message.setSubject(subject);
						message.setText(content, isHtml);
						sender.send(mimeMessage);
						log.debug("Sent e-mail to User '{}'", to);
					} catch (Exception e) {
						log.warn(
								"E-mail could not be sent to user '{}', exception is: {}",
								to, e.getMessage());
					}
				}

			};
		}

	}
}
