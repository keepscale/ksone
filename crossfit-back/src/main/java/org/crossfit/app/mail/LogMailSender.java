package org.crossfit.app.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

public class LogMailSender implements MailSender {

	private final Logger log = LoggerFactory.getLogger(LogMailSender.class);
	
	
	public LogMailSender() {
		super();
	}

	@Override
	public void sendEmail(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		sendEmail(new Email(from, to, subject, content, isMultipart, isHtml));
	}

	@Async
	@Override
	public void sendEmail(Email email) {
		try {
			log.debug("Attente de 5s avant d'envoyer le mail.");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		log.info("Send mail to {} from {} with subject {} and content {} and {} attachments", 
				email.getTo(), email.getFrom(), email.getSubject(), email.getContent(), email.getAttachments().size());
	}

}
