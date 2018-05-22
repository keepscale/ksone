package org.crossfit.app.web.rest.admin.dto;

public class Release {

	private String tagName;
	private String jarName;
	private String downloadUrl;
	public Release(String tagName, String jarName, String downloadUrl) {
		super();
		this.tagName = tagName;
		this.jarName = jarName;
		this.downloadUrl = downloadUrl;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getJarName() {
		return jarName;
	}
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	@Override
	public String toString() {
		return "Release [tagName=" + tagName + ", jarName=" + jarName + ", downloadUrl=" + downloadUrl + "]";
	}
		
	
}
