package org.crossfit.app.mail;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.sendgrid.Content;
import com.sendgrid.Email;
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
	
	@Async
	@Override
	public void sendEmail(String fromStr, String toStr, String subject, String contentStr, boolean isMultipart, boolean isHtml) {
		
		Email from = new Email(fromStr);
		Email to = new Email(toStr);
		Content content = new Content(isHtml ? "text/html" : "text/plain", contentStr);

		Mail mail = new Mail(from, subject, to, content);

		SendGrid sg = new SendGrid(apiKey);
		Request request = new Request();
		try {
			
			log.debug("Send mail to {} from {} with subject {} and content {}", to, from, subject, content);
			
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
