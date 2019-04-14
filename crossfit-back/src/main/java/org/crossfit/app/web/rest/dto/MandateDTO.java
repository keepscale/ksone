package org.crossfit.app.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.enumeration.MandateStatus;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.crossfit.app.web.rest.api.MembershipResource;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;


/**
 * A MembershipType.
 */
public class MandateDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public static Function<Mandate, MandateDTO> fullMapper = s->{
		MandateDTO dto = new MandateDTO();
		dto.setId(s.getId());
		dto.setStatus(s.getStatus());
		dto.setRum(s.getRum());
		dto.setIcs(s.getIcs());
		dto.setIban(s.getIban());
		dto.setBic(s.getBic());
		dto.setSignatureDataEncoded(s.getSignatureDataEncoded());
		dto.setSignatureDate(s.getSignatureDate());
		return dto;
	};
	
    private Long id;

    private MandateStatus status;

	@Size(max = 35)
	private String rum;

	@Size(max = 64)
	private String ics;

	@Size(max = 34)
	private String iban;

	@Size(max = 8)
	private String bic;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime signatureDate;
    
    private String signatureDataEncoded;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MandateDTO that = (MandateDTO) o;
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

	public MandateStatus getStatus() {
		return status;
	}

	public void setStatus(MandateStatus status) {
		this.status = status;
	}

	public String getRum() {
		return rum;
	}

	public void setRum(String rum) {
		this.rum = rum;
	}

	public String getIcs() {
		return ics;
	}

	public void setIcs(String ics) {
		this.ics = ics;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public DateTime getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(DateTime signatureDate) {
		this.signatureDate = signatureDate;
	}

	public String getSignatureDataEncoded() {
		return signatureDataEncoded;
	}

	public void setSignatureDataEncoded(String signatureDataEncoded) {
		this.signatureDataEncoded = signatureDataEncoded;
	}
}
