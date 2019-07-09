package org.crossfit.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * A SubscriptionDirectDebit.
 */
@Entity
@Table(name = "SUBSCRIPTION_DIRECT_DEBIT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SubscriptionDirectDebit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional=false, cascade = {})
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY, optional=true, cascade = {})
    private Mandate mandate;

    @Column(name = "first_payment_tax_incl")
    private Double firstPaymentTaxIncl;

    @Column(name = "first_payment_method")
    private PaymentMethod firstPaymentMethod;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @Column(name = "after_date")
    private LocalDate afterDate;

    @Column(name = "at_day_of_month")
    private Integer atDayOfMonth;

	@NotNull
	@Column(name = "amount", nullable=false)
	private Double amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionDirectDebit that = (SubscriptionDirectDebit) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Mandate getMandate() {
        return mandate;
    }

    public void setMandate(Mandate mandate) {
        this.mandate = mandate;
    }

    public Double getFirstPaymentTaxIncl() {
        return firstPaymentTaxIncl;
    }

    public void setFirstPaymentTaxIncl(Double firstPaymentTaxIncl) {
        this.firstPaymentTaxIncl = firstPaymentTaxIncl;
    }

    public PaymentMethod getFirstPaymentMethod() {
        return firstPaymentMethod;
    }

    public void setFirstPaymentMethod(PaymentMethod firstPaymentMethod) {
        this.firstPaymentMethod = firstPaymentMethod;
    }

    public LocalDate getAfterDate() {
        return afterDate;
    }

    public void setAfterDate(LocalDate afterDate) {
        this.afterDate = afterDate;
    }

    public Integer getAtDayOfMonth() {
        return atDayOfMonth;
    }

    public void setAtDayOfMonth(Integer atDayOfMonth) {
        this.atDayOfMonth = atDayOfMonth;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
