package org.crossfit.app.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.crossfit.app.domain.SubscriptionContractModel;
import org.crossfit.app.domain.SubscriptionDirectDebit;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;


/**
 * A SubscriptionContractModelDTO.
 */
public class SubscriptionContractModelDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	

	public static Function<SubscriptionContractModel, SubscriptionContractModelDTO> fullMapper = s->{
		SubscriptionContractModelDTO dto = new SubscriptionContractModelDTO();
		dto.setId(s.getId());
		dto.setVersionData(s.getVersionData());
		dto.setVersionFormat(s.getVersionFormat());
		dto.setJsonValue(s.getJsonValue());
		return dto;
	};
	
    private Long id;

	private String versionData;

	private VersionFormatContractSubscription versionFormat;

	private String jsonValue;


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SubscriptionContractModelDTO that = (SubscriptionContractModelDTO) o;
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

	public String getVersionData() {
		return versionData;
	}

	public void setVersionData(String versionData) {
		this.versionData = versionData;
	}

	public VersionFormatContractSubscription getVersionFormat() {
		return versionFormat;
	}

	public void setVersionFormat(VersionFormatContractSubscription versionFormat) {
		this.versionFormat = versionFormat;
	}

	public String getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(String jsonValue) {
		this.jsonValue = jsonValue;
	}
}
