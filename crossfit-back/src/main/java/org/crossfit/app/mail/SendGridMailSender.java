package org.crossfit.app.mail;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class SendGridMailSender implements MailSender {

	private final Logger log = LoggerFactory.getLogger(SendGridMailSender.class);
	
	private final String apiKey;
	
	public SendGridMailSender(String apiKey) {
		super();
		this.apiKey = apiKey;
	}
	
	@Override
	public void sendEmail(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		sendEmail(new Email(from, to, subject, content, isMultipart, isHtml));
	}

	@Async
	@Override
	public void sendEmail(Email email) {
		
		com.sendgrid.Email from = new com.sendgrid.Email(email.getFrom());
		com.sendgrid.Email to = new com.sendgrid.Email(email.getTo());
		Content content = new Content(email.isHtml() ? "text/html" : "text/plain", email.getContent());

		Mail mail = new Mail(from, email.getSubject(), to, content);
		for (EmailAttachment a : email.getAttachments()) {

			Attachments attachments = new Attachments();
			attachments.setContent(a.getContent());
			attachments.setContentId(a.getContentId());
			attachments.setDisposition(a.getDisposition());
			attachments.setFilename(a.getFileName());
			attachments.setType(a.getType());
			mail.addAttachments(attachments);
		}

		SendGrid sg = new SendGrid(apiKey);
		Request request = new Request();
		try {
			
			log.debug("Send mail to {} from {} with subject {} and content {}", to, from, email.getSubject(), content);
			
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			
			log.info("Email sent to '{}'. {} {} / Headers: {}", to, response.getStatusCode(), response.getBody(), response.getHeaders());

		} catch (IOException e) {
			log.error("E-mail could not be sent to '{}' with SendGrid: {}", to, e.getMessage(), e);
		}
	}

}
