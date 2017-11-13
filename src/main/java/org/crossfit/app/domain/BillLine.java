package org.crossfit.app.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * A MembershipType.
 */
@Entity
@Table(name = "BILL_LINE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BillLine extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @JsonIgnore
    @ManyToOne(optional=false, cascade = {})
    private Bill bill;

    @Size(max = 255)
    @Column(name = "label", length = 255)
    private String label;
    
    @Column(name = "quantity")
    private double quantity;
    
    @Column(name = "price_tax_excl")
    private double priceTaxExcl;
    
    @Column(name = "price_tax_incl")
    private double priceTaxIncl;
    
    @Column(name = "total_tax_excl")
    private double totalTaxExcl;    

    @Column(name = "tax_per_cent")
    private double taxPerCent;
    
    @Column(name = "total_tax_incl")
    private double totalTaxIncl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getPriceTaxExcl() {
		return priceTaxExcl;
	}

	public void setPriceTaxExcl(double priceTaxExcl) {
		this.priceTaxExcl = priceTaxExcl;
	}

	public double getPriceTaxIncl() {
		return priceTaxIncl;
	}

	public void setPriceTaxIncl(double priceTaxIncl) {
		this.priceTaxIncl = priceTaxIncl;
	}

	public double getTotalTaxExcl() {
		return totalTaxExcl;
	}

	public void setTotalTaxExcl(double totalTaxExcl) {
		this.totalTaxExcl = totalTaxExcl;
	}

	public double getTaxPerCent() {
		return taxPerCent;
	}

	public void setTaxPerCent(double taxPerCent) {
		this.taxPerCent = taxPerCent;
	}

	public double getTotalTaxIncl() {
		return totalTaxIncl;
	}

	public void setTotalTaxIncl(double totalTaxIncl) {
		this.totalTaxIncl = totalTaxIncl;
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
		BillLine other = (BillLine) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BillLine [id=" + id + ", label=" + label + ", quantity=" + quantity + ", priceTaxExcl=" + priceTaxExcl
				+ ", priceTaxIncl=" + priceTaxIncl + ", totalTaxExcl=" + totalTaxExcl + ", taxPerCent=" + taxPerCent
				+ ", totalTaxIncl=" + totalTaxIncl + "]";
	}
    
}
