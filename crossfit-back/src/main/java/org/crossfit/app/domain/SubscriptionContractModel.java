package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * A MembershipType.
 */
@Entity
@Table(name = "SUBSCRIPTION_CONTRACT_MODEL")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SubscriptionContractModel extends AbstractAuditingEntity implements Serializable {


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
    @Basic(fetch=FetchType.LAZY)
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
        SubscriptionContractModel that = (SubscriptionContractModel) o;
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
