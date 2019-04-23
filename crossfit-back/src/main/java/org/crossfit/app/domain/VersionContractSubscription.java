package org.crossfit.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;
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
 * A MembershipType.
 */
@Entity
@Table(name = "VERSION_CONTRACT_SUBSCRIPTION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class VersionContractSubscription extends AbstractAuditingEntity implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@ManyToOne(optional=false)
	private CrossFitBox box;

    @NotNull
    @Column(name = "version_data", nullable = false)
    private String versionData;

    @NotNull
	@Column(name = "version_format", nullable = false)
    private VersionFormatContractSubscription versionFormat = VersionFormatContractSubscription.V_1;


    @Column(name = "json_value")
    @JsonIgnore
    private String jsonValue;
    

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
        VersionContractSubscription that = (VersionContractSubscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public CrossFitBox getBox() {
        return box;
    }

    public void setBox(CrossFitBox box) {
        this.box = box;
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
