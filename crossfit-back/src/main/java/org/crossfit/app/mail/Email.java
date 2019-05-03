package org.crossfit.app.mail;

import java.util.ArrayList;
import java.util.List;

public class Email {
	private final String from;
	private final String to;
	private final String subject;
	private final String content;
	private final boolean isMultipart;
	private final boolean isHtml;

	private final List<EmailAttachment> attachments = new ArrayList<>();

	public Email(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		super();
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.isMultipart = isMultipart;
		this.isHtml = isHtml;
	}

	public void addAttachment(EmailAttachment attachment) {
		this.attachments.add(attachment);
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	public boolean isMultipart() {
		return isMultipart;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public List<EmailAttachment> getAttachments() {
		return attachments;
	}

}
