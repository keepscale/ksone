package org.crossfit.app.web.rest.card.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.enumeration.Title;
import org.crossfit.app.web.rest.dto.BookingDTO;

public class MemberCardDTO {

	private static final long serialVersionUID = 1L;

	
    private Long id;

    private Title title;

    private String firstName;

    private String lastName;
    
    private String cardUuid;

    private String email;
    
    private boolean locked;
    
    private boolean enabled;

    private List<BookingDTO> bookings;

    public MemberCardDTO(Member m, Collection<? extends BookingDTO> bookings){
    	this.id = m.getId();
    	this.title = m.getTitle();
    	this.firstName = m.getFirstName();
    	this.lastName = m.getLastName();
    	this.cardUuid = m.getCardUuid();
    	this.email = m.getLogin();
    	this.locked = m.isLocked();
    	this.enabled = m.isEnabled();
    	this.bookings = new ArrayList<>(bookings);
    }
    
    
    public void addBooking(BookingDTO b){
    	this.bookings.add(b);
    }
    
	public Long getId() {
		return id;
	}

	public Title getTitle() {
		return title;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getCardUuid() {
		return cardUuid;
	}

	public String getEmail() {
		return email;
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public List<BookingDTO> getBookings() {
		return bookings;
	}

	@Override
	public String toString() {
		return "MemberCardDTO [id=" + id + ", title=" + title + ", firstName="
				+ firstName + ", lastName=" + lastName + ", cardUuid="
				+ cardUuid + ", email=" + email + ", locked=" + locked
				+ ", enabled=" + enabled + ", bookings=" + bookings + "]";
	}

    
	
}
