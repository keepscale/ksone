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
@Table(name = "MEMBERSHIP")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Membership extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull        
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull        
    @Column(name = "price_tax_incl", nullable=false)
    private double priceTaxIncl;

    @NotNull
    @Column(name = "tax_per_cent", nullable = false)
    private double taxPerCent;

    @NotNull        
    @Column(name = "add_by_default", nullable = false)
    private boolean addByDefault = false;

    @ManyToOne(optional=false)
    private CrossFitBox box;

    @NotNull        
    @Column(name = "nb_month_validity", nullable = false)
    private int nbMonthValidity;
    
    @OneToMany(mappedBy="membership", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
    private Set<MembershipRules> membershipRules = new HashSet<>();


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

	public int getNbMonthValidity() {
		return nbMonthValidity;
	}

	public void setNbMonthValidity(int nbMonthValidity) {
		this.nbMonthValidity = nbMonthValidity;
	}

	public CrossFitBox getBox() {
        return box;
    }

    public void setBox(CrossFitBox crossFitBox) {
        this.box = crossFitBox;
    }
        
	public Set<MembershipRules> getMembershipRules() {
		return membershipRules;
	}

	public void setMembershipRules(Set<MembershipRules> membershipRules) {
		this.membershipRules = membershipRules;
	}
	

	public boolean isAddByDefault() {
		return addByDefault;
	}

	public void setAddByDefault(boolean addByDefault) {
		this.addByDefault = addByDefault;
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

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Membership membershipType = (Membership) o;

        if ( ! Objects.equals(id, membershipType.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Membership{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", price='" + priceTaxIncl + "'" +
                '}';
    }
}
