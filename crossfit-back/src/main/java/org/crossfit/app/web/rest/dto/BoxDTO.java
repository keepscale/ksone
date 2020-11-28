package org.crossfit.app.web.rest.dto;

import org.crossfit.app.domain.CrossFitBox;

public class BoxDTO {
	
	private String name;

    private String logoUrl;
    private String bookingUrl;
    

	public BoxDTO(CrossFitBox box) {
    	this.name = box.getName();
    	this.logoUrl = box.getLogoUrl();
    	this.bookingUrl = box.getBookingwebsite();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getBookingUrl() {
		return bookingUrl;
	}

	public void setBookingUrl(String bookingUrl) {
		this.bookingUrl = bookingUrl;
	}

	
}
