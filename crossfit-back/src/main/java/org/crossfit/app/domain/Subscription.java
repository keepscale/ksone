package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * A MembershipType.
 */
@Entity
@Table(name = "SUBSCRIPTION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Subscription extends AbstractAuditingEntity implements Serializable, Signable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(optional=false, cascade = {})
    private Member member;

    @NotNull
    @ManyToOne(optional=false, cascade = {})
    private Membership membership;
    
    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "subscription_start_date", nullable = false)
    private LocalDate subscriptionStartDate;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "subscription_end_date", nullable = false)
    private LocalDate subscriptionEndDate;

    @NotNull        
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @JoinColumn(name = "subscription_direct_debit_id")
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private SubscriptionDirectDebit directDebit;

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "signature_data_base64")
    @JsonIgnore
    private String signatureDataEncoded;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "signature_date")
    private DateTime signatureDate;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", membre='" + (member == null ? "null" : member.getId() )+ "'" +
                ", membership='" + (membership == null ? "null" : membership.getId()) + "'" +
                ", startDate='" + subscriptionStartDate + "'" +
                ", endDate='" + subscriptionEndDate + "'" +
                '}';
    }

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
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

	public SubscriptionDirectDebit getDirectDebit() {
		return directDebit;
	}

	public void setDirectDebit(SubscriptionDirectDebit directDebit) {
		this.directDebit = directDebit;
	}

	public String getSignatureDataEncoded() {
		return signatureDataEncoded;
	}

	public void setSignatureDataEncoded(String signatureDataEncoded) {
		this.signatureDataEncoded = signatureDataEncoded;
	}

	public DateTime getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(DateTime signatureDate) {
		this.signatureDate = signatureDate;
	}
    
}
