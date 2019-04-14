package org.crossfit.app.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.SubscriptionDirectDebit;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.crossfit.app.web.rest.api.MembershipResource;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;


/**
 * A MembershipType.
 */
public class SubscriptionDirectDebitDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public static Function<SubscriptionDirectDebit, SubscriptionDirectDebitDTO> fullMapper = s->{
		SubscriptionDirectDebitDTO dto = new SubscriptionDirectDebitDTO();
		dto.setId(s.getId());
		dto.setMandate(MandateDTO.fullMapper.apply(s.getMandate()));
		dto.setFirstPaymentTaxIncl(s.getFirstPaymentTaxIncl());
		dto.setFirstPaymentMethod(s.getFirstPaymentMethod());
		dto.setAfterDate(s.getAfterDate());
		dto.setAtDayOfMonth(s.getAtDayOfMonth());
		dto.setAmount(s.getAmount());
		return dto;
	};
	
    private Long id;

    private MandateDTO mandate;

    private Double firstPaymentTaxIncl;
    
    private PaymentMethod firstPaymentMethod;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate afterDate;
    private Integer atDayOfMonth;
	private Double amount;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SubscriptionDirectDebitDTO that = (SubscriptionDirectDebitDTO) o;
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

	public MandateDTO getMandate() {
		return mandate;
	}

	public void setMandate(MandateDTO mandate) {
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
