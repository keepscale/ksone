package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * A CrossFitBox.
 */
@Entity
@Table(name = "CROSSFITBOX")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CrossFitBox implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    

    @NotNull        
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "bill_name", nullable = false)
    private String billName;

    @Column(name = "bill_address", nullable = true)
    private String billAddress;

    @Column(name = "bill_logo_url", nullable = true)
    private String billLogoUrl;
    
    @Column(name = "bill_footer", nullable = true)
    private String billFooter;

    @NotNull        
    @Column(name = "website_pattern", nullable = false)
    private String websitepattern;

    @NotNull        
    @Column(name = "adminwebsite", nullable = false)
    private String adminwebsite;

    @NotNull        
    @Column(name = "bookingwebsite", nullable = false)
    private String bookingwebsite;

    @NotNull        
    @Column(name = "rootwebsite", nullable = false)
    private String rootwebsite;

    @javax.validation.constraints.Email
    @NotNull        
    @Column(name = "email_from", nullable = false)
    private String emailFrom;
    
    @NotNull        
    @Column(name = "logo_url", nullable = true)
    private String logoUrl;
    
    @Column(name = "time_zone", nullable = true)
    private String timeZone;

    @NotNull        
    @Column(name = "social_enabled", nullable = false)
    private boolean socialEnabled = false;
    
    @Size(max=255)
    @Column(name = "redirect_to_rules", nullable = true)
    private String redirectToRules;

    @NotNull        
    @Column(name = "alert_when_med_cert_expires_in_days", nullable = false)
    private int alertWhenMedicalCertificateExpiresInDays;

    @Size(max=64)
    @Column(name = "default_ics", nullable = true)
    private String defautICS;

    @Column(name = "json_mandate")
    private String jsonMandate;

    @javax.validation.constraints.Email
    @NotNull
    @Column(name = "to_email_contract")
    private String toEmailContract;

    @javax.validation.constraints.Email
    @NotNull
    @Column(name = "to_email_mandate")
    private String toEmailMandate;

    @NotNull        
    @Column(name = "mandate_mandatory", nullable = false)
    private boolean mandateMandatory = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsitepattern() {
        return websitepattern;
    }

    public void setWebsitepattern(String websitepattern) {
        this.websitepattern = websitepattern;
    }

    public String getAdminwebsite() {
        return adminwebsite;
    }

    public void setAdminwebsite(String adminwebsite) {
        this.adminwebsite = adminwebsite;
    }

    public String getBookingwebsite() {
        return bookingwebsite;
    }

    public void setBookingwebsite(String bookingwebsite) {
        this.bookingwebsite = bookingwebsite;
    }

    public String getRootwebsite() {
        return rootwebsite;
    }

    public void setRootwebsite(String rootwebsite) {
        this.rootwebsite = rootwebsite;
    }
    
	public boolean isSocialEnabled() {
		return socialEnabled;
	}

	public void setSocialEnabled(boolean socialEnabled) {
		this.socialEnabled = socialEnabled;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getRedirectToRules() {
		return redirectToRules;
	}

	public void setRedirectToRules(String redirectToRules) {
		this.redirectToRules = redirectToRules;
	}

	public String getBillName() {
		return billName;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}

	public String getBillAddress() {
		return billAddress;
	}

	public void setBillAddress(String billAddress) {
		this.billAddress = billAddress;
	}

	public String getBillLogoUrl() {
		return billLogoUrl;
	}

	public void setBillLogoUrl(String billLogoUrl) {
		this.billLogoUrl = billLogoUrl;
	}

	public String getBillFooter() {
		return billFooter;
	}

	public void setBillFooter(String billFooter) {
		this.billFooter = billFooter;
	}
	
	

	public int getAlertWhenMedicalCertificateExpiresInDays() {
		return alertWhenMedicalCertificateExpiresInDays;
	}

	public void setAlertWhenMedicalCertificateExpiresInDays(int alertWhenMedicalCertificateExpiresInDays) {
		this.alertWhenMedicalCertificateExpiresInDays = alertWhenMedicalCertificateExpiresInDays;
	}

    public String getDefautICS() {
        return defautICS;
    }

    public void setDefautICS(String defautICS) {
        this.defautICS = defautICS;
    }

    public String getJsonMandate() {
        return jsonMandate;
    }

    public void setJsonMandate(String jsonMandate) {
        this.jsonMandate = jsonMandate;
    }

    public String getToEmailContract() {
        return toEmailContract;
    }

    public void setToEmailContract(String toEmailContract) {
        this.toEmailContract = toEmailContract;
    }

    public String getToEmailMandate() {
        return toEmailMandate;
    }

    public void setToEmailMandate(String toEmailMandate) {
        this.toEmailMandate = toEmailMandate;
    }
    
    public boolean isMandateMandatory() {
		return mandateMandatory;
	}

	public void setMandateMandatory(boolean mandateMandatory) {
		this.mandateMandatory = mandateMandatory;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CrossFitBox crossFitBox = (CrossFitBox) o;

        if ( ! Objects.equals(id, crossFitBox.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CrossFitBox{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", websitepattern='" + websitepattern + "'" +
                ", adminwebsite='" + adminwebsite + "'" +
                ", bookingwebsite='" + bookingwebsite + "'" +
                ", rootwebsite='" + rootwebsite + "'" +
                '}';
    }

	public String getTimeZoneId() {
		return TimeZone.getTimeZone(StringUtils.isEmpty(getTimeZone()) ? "Europe/Paris" : getTimeZone()).getID();
	}
}
