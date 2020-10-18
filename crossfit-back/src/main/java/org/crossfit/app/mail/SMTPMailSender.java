package org.crossfit.app.mail;


import java.io.ByteArrayInputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sendgrid.Method;
import com.sendgrid.Response;

public class SMTPMailSender implements MailSender {

	private final Logger log = LoggerFactory.getLogger(SMTPMailSender.class);
	
    @Autowired
    private JavaMailSender emailSender;

	@Override
	public void sendEmail(Email mail) {
		log.debug("Send mail to {} from {} with subject {} and content {}", mail.getTo(), mail.getFrom(), mail.getSubject(), mail.getContent());
		
        try {
			MimeMessage message = emailSender.createMimeMessage();
			 
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			helper.setFrom(mail.getFrom());
			helper.setTo(mail.getTo().toArray(new String[] {}));
			helper.setSubject(mail.getSubject());
			helper.setText(mail.getContent(), mail.isHtml());
			
			for (EmailAttachment attachment : mail.getAttachments()) {
				helper.addAttachment(
						attachment.getFileName(), 
						new ByteArrayResource(mail.getContent().getBytes()),
						attachment.getType());
			}
    
			emailSender.send(message);
			
			log.info("Email sent to '{}'.", mail.getTo());

		} catch (MailException | MessagingException e) {
			log.error("E-mail could not be sent to '{}' with SMTP: {}", mail.getTo(), e.getMessage(), e);
		}
	}

	@Override
	public void sendEmail(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		sendEmail(new Email(from, to, subject, content, isMultipart, isHtml));
	}

}
