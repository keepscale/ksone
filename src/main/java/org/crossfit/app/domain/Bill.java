package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crossfit.app.domain.enumeration.BillStatus;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
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
 * A Member.
 */
@Entity
@Table(name = "BILL")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Bill extends AbstractAuditingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional=false, cascade = {})
    private CrossFitBox box;
    
    @NotNull
    @ManyToOne(optional=false, cascade = {})
    private Member member;

    @NotNull        
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BillStatus status;

    @NotNull        
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Size(max = 36)
    @Column(name = "number", length = 36)
    private String number;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "effective_date", nullable = true)
    private LocalDate effectiveDate;
    
    @Size(max = 120)
    @Column(name = "display_name", length = 120)
    private String displayName;
    
    @Size(max = 255)
    @Column(name = "display_address", length = 255)
    private String displayAddress;

    @Column(name = "total_tax_excl")
    private double totalTaxExcl;
    
    @Column(name = "total_tax_incl")
    private double totalTaxIncl;
    
    @Size(max = 255)
    @Column(name = "comments", length = 255)
    private String comments;
    
    @OneToMany(mappedBy="bill", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
    private List<BillLine> lines = new ArrayList<>();

    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
	public DateTime getCreatedBillDate() {
		return super.getCreatedDate();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CrossFitBox getBox() {
		return box;
	}

	public void setBox(CrossFitBox box) {
		this.box = box;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}


	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayAddress() {
		return displayAddress;
	}

	public void setDisplayAddress(String displayAddress) {
		this.displayAddress = displayAddress;
	}

	public double getTotalTaxExcl() {
		return totalTaxExcl;
	}

	public void setTotalTaxExcl(double totalTaxExcl) {
		this.totalTaxExcl = totalTaxExcl;
	}

	public double getTotalTaxIncl() {
		return totalTaxIncl;
	}

	public void setTotalTaxIncl(double totalTaxIncl) {
		this.totalTaxIncl = totalTaxIncl;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<BillLine> getLines() {
		return lines;
	}

	public void setLines(List<BillLine> lines) {
		this.lines = lines;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bill other = (Bill) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bill [id=" + id + ", status=" + status + ", number=" + number + ", effectiveDate=" + effectiveDate
				+ ", displayName=" + displayName + ", displayAddress=" + displayAddress + ", totalTaxExcl="
				+ totalTaxExcl + ", totalTaxIncl=" + totalTaxIncl + ", comments=" + comments + ", lines=" + lines + "]";
	}
    
}
