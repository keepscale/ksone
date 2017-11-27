package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * A MembershipType.
 */
@Entity
@Table(name = "PRODUCT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Product extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull        
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull        
    @Column(name = "price_tax_incl", nullable=false)
    private double priceTaxIncl;

    @NotNull
    @Column(name = "tax_per_cent", nullable = false)
    private double taxPerCent;

    @JsonIgnore
    @ManyToOne(optional=false)
    private CrossFitBox box;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public double getPriceTaxIncl() {
		return priceTaxIncl;
	}

	public void setPriceTaxIncl(double priceTaxIncl) {
		this.priceTaxIncl = priceTaxIncl;
	}

	public double getTaxPerCent() {
		return taxPerCent;
	}

	public void setTaxPerCent(double taxPerCent) {
		this.taxPerCent = taxPerCent;
	}

	public CrossFitBox getBox() {
		return box;
	}

	public void setBox(CrossFitBox box) {
		this.box = box;
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
		Product other = (Product) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", priceTaxIncl=" + priceTaxIncl + ", taxPerCent=" + taxPerCent
				+ "]";
	}
    
}
