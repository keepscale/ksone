package org.crossfit.app.web.rest.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
		dto.setPaymentMethod(s.getPaymentMethod());
		
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

    @NotNull
    private PaymentMethod paymentMethod;

    private Long bookingCount;
    private int maxCount;
    
    
    @Size(max = 34)
    private String directDebitIban;

    @Size(max = 8)
    private String directDebitBic;

    private Double directDebitFirstPaymentTaxIncl;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate directDebitAfterDate;
    
    private Integer directDebitAtDayOfMonth;

    
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime signatureDate;
        
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

	
	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
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

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getDirectDebitIban() {
		return directDebitIban;
	}

	public void setDirectDebitIban(String directDebitIban) {
		this.directDebitIban = directDebitIban;
	}

	public String getDirectDebitBic() {
		return directDebitBic;
	}

	public void setDirectDebitBic(String directDebitBic) {
		this.directDebitBic = directDebitBic;
	}

	public Double getDirectDebitFirstPaymentTaxIncl() {
		return directDebitFirstPaymentTaxIncl;
	}

	public void setDirectDebitFirstPaymentTaxIncl(Double directDebitFirstPaymentTaxIncl) {
		this.directDebitFirstPaymentTaxIncl = directDebitFirstPaymentTaxIncl;
	}

	public LocalDate getDirectDebitAfterDate() {
		return directDebitAfterDate;
	}

	public void setDirectDebitAfterDate(LocalDate directDebitAfterDate) {
		this.directDebitAfterDate = directDebitAfterDate;
	}

	public Integer getDirectDebitAtDayOfMonth() {
		return directDebitAtDayOfMonth;
	}

	public void setDirectDebitAtDayOfMonth(Integer directDebitAtDayOfMonth) {
		this.directDebitAtDayOfMonth = directDebitAtDayOfMonth;
	}

	public DateTime getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(DateTime signatureDate) {
		this.signatureDate = signatureDate;
	}
}
