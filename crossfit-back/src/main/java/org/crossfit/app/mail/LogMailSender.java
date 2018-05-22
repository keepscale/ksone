package org.crossfit.app.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMailSender implements MailSender {

	private final Logger log = LoggerFactory.getLogger(LogMailSender.class);
	
	
	public LogMailSender() {
		super();
	}

	@Override
	public void sendEmail(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.info("Send mail to {} from {} with subject {} and content {}", to, from, subject, content);
	}

}
