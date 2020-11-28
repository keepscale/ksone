package org.crossfit.app.web.rest.dto;

import org.crossfit.app.domain.CrossFitBox;

public class SimpleBoxDTO extends BoxDTO {
	

    private String billLogoUrl;
    private boolean socialEnabled = false;
    private boolean mandateMandatory = true;

	private String defautICS;


	public SimpleBoxDTO(CrossFitBox box) {
    	super(box);
    	this.billLogoUrl = box.getBillLogoUrl();
    	this.socialEnabled = box.isSocialEnabled();
    	this.defautICS = box.getDefautICS();
    	this.mandateMandatory = box.isMandateMandatory();
    }

	public String getBillLogoUrl() {
		return billLogoUrl;
	}

	public void setBillLogoUrl(String billLogoUrl) {
		this.billLogoUrl = billLogoUrl;
	}

	public boolean isSocialEnabled() {
		return socialEnabled;
	}

	public void setSocialEnabled(boolean socialEnabled) {
		this.socialEnabled = socialEnabled;
	}

	public String getDefautICS() {
		return defautICS;
	}

	public void setDefautICS(String defautICS) {
		this.defautICS = defautICS;
	}

	public boolean isMandateMandatory() {
		return mandateMandatory;
	}

	public void setMandateMandatory(boolean mandateMandatory) {
		this.mandateMandatory = mandateMandatory;
	}
	
}
