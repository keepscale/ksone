package org.crossfit.app.mail;

public class EmailAttachment {
	private final String content;
	private final String contentId;
	private final String disposition;
	private final String fileName;
	private final String type;
	
	public EmailAttachment(String content, String contentId, String disposition, String fileName, String type) {
		super();
		this.content = content;
		this.contentId = contentId;
		this.disposition = disposition;
		this.fileName = fileName;
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public String getContentId() {
		return contentId;
	}

	public String getDisposition() {
		return disposition;
	}

	public String getFileName() {
		return fileName;
	}

	public String getType() {
		return type;
	}
	
}
