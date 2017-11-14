package org.crossfit.app.web.rest.dto.bills;

import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.enumeration.BillStatus;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BillGenerationParamDTO {

    @NotNull        
	private Integer atDayOfMonth;
	

    @NotNull        
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate sinceDate;    

    @NotNull        
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate untilDate;   

    private BillStatus status;    
    private PaymentMethod	paymentMethod;

    
    
	public Integer getAtDayOfMonth() {
		return atDayOfMonth;
	}

	public void setAtDayOfMonth(Integer atDayOfMonth) {
		this.atDayOfMonth = atDayOfMonth;
	}

	public LocalDate getSinceDate() {
		return sinceDate;
	}

	public void setSinceDate(LocalDate sinceDate) {
		this.sinceDate = sinceDate;
	}

	public LocalDate getUntilDate() {
		return untilDate;
	}

	public void setUntilDate(LocalDate untilDate) {
		this.untilDate = untilDate;
	}

	public BillStatus getStatus() {
		return status;
	}

	public void setStatus(BillStatus status) {
		this.status = status;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Override
	public String toString() {
		return "BillGenerationParamDTO [atDayOfMonth=" + atDayOfMonth + ", sinceDate=" + sinceDate + ", untilDate="
				+ untilDate + ", status=" + status + ", paymentMethod=" + paymentMethod + "]";
	}
	
	
}