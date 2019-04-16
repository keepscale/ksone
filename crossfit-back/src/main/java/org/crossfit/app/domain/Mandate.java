package org.crossfit.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.crossfit.app.domain.enumeration.MandateStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "MANDATE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Mandate extends AbstractAuditingEntity implements Serializable, Signable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional=false, cascade = {})
    private Member member;

    @Column(name = "rum", length = 35)
    private String rum; //reference unique mandat

    @Column(name = "ics", length = 64)
    private String ics; //identifiant creancier sepa

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MandateStatus status = MandateStatus.PENDING_CUSTOMER_APPROVAL;

    @Size(max = 34)
    @Column(name = "iban", length = 34)
    private String iban;

    @Size(max = 8)
    @Column(name = "bic", length = 8)
    private String bic;

    @Basic(fetch= FetchType.LAZY)
    @Column(name = "signature_data_base64")
    @JsonIgnore
    private String signatureDataEncoded;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "signature_date")
    private DateTime signatureDate;

    //private String externalReference; //Si mandat externalisé

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mandate mandate = (Mandate) o;
        return Objects.equals(id, mandate.id);
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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
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
