package org.crossfit.app.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.Title;
import org.hibernate.validator.constraints.Email;

public class MemberDTO {

	private static final long serialVersionUID = 1L;
    private Long id;

    @NotNull
    private Title title;
    
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(max = 255)
    private String address;

    @Size(max = 20)
    private String zipCode;
    
    @Size(max = 100)
    private String city;
    
    @Size(max = 32)
    private String telephonNumber;
    

    @NotNull
    @Email
    @Size(max = 100)
    private String email;
    
    private boolean locked;
    
    private boolean enabled;

    @Size(min = 2, max = 5)
    private String langKey;


    private List<Subscription> subscriptions = new ArrayList<>();


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Title getTitle() {
		return title;
	}


	public void setTitle(Title title) {
		this.title = title;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getZipCode() {
		return zipCode;
	}


	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getTelephonNumber() {
		return telephonNumber;
	}


	public void setTelephonNumber(String telephonNumber) {
		this.telephonNumber = telephonNumber;
	}

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public boolean isLocked() {
		return locked;
	}


	public void setLocked(boolean locked) {
		this.locked = locked;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public String getLangKey() {
		return langKey;
	}


	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}


	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}


	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}


	@Override
	public String toString() {
		return "MemberDTO [id=" + id + ", title=" + title + ", firstName="
				+ firstName + ", lastName=" + lastName + ", address=" + address
				+ ", zipCode=" + zipCode + ", city=" + city
				+ ", telephonNumber=" + telephonNumber + ", email=" + email
				+ ", locked=" + locked + ", enabled=" + enabled + ", langKey="
				+ langKey + ", subscriptions=" + subscriptions + "]";
	}

	
	
}
