package org.crossfit.app.web.rest.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * A MembershipType.
 */
public class SubscriptionDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public static Function<Subscription, SubscriptionDTO> fullMapper = s->{
		SubscriptionDTO dto = new SubscriptionDTO();
		dto.setId(s.getId());
		dto.setMembership(s.getMembership());
		dto.setSubscriptionStartDate(s.getSubscriptionStartDate());
		dto.setSubscriptionEndDate(s.getSubscriptionEndDate());
		
		return dto;
	};
	
    private Long id;

    @NotNull
    private Membership membership;
    
    @NotNull        
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate subscriptionStartDate;
    
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate subscriptionEndDate;

    private Long bookingCount;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    public Long getBookingCount() {
		return bookingCount;
	}

	public void setBookingCount(Long bookingCount) {
		this.bookingCount = bookingCount;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SubscriptionDTO subscription = (SubscriptionDTO) o;

        if ( ! Objects.equals(id, subscription.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	public LocalDate getSubscriptionStartDate() {
		return subscriptionStartDate;
	}

	public void setSubscriptionStartDate(LocalDate subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}

	public LocalDate getSubscriptionEndDate() {
		return subscriptionEndDate;
	}

	public void setSubscriptionEndDate(LocalDate subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}
    
    
}
