package org.crossfit.app.web.rest.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.MembershipRules;


/**
 * A MembershipDTO.
 */
public class MembershipDTO implements Serializable {

    private Long id;

    @NotNull        
    private String name;
     
    private String information;
    
    private String resiliationInformation;
    
    @NotNull        
    private double priceTaxIncl;

    @NotNull     
    private double taxPerCent;

    @NotNull        
    private boolean addByDefault;
    
    @NotNull        
    private int nbMonthValidity;
    
    
    
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
    
	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getResiliationInformation() {
		return resiliationInformation;
	}

	public void setResiliationInformation(String resiliationInformation) {
		this.resiliationInformation = resiliationInformation;
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

	public boolean isAddByDefault() {
		return addByDefault;
	}

	public void setAddByDefault(boolean addByDefault) {
		this.addByDefault = addByDefault;
	}

	public int getNbMonthValidity() {
		return nbMonthValidity;
	}

	public void setNbMonthValidity(int nbMonthValidity) {
		this.nbMonthValidity = nbMonthValidity;
	}

	public Set<MembershipRules> getMembershipRules() {
		return membershipRules;
	}

	public void setMembershipRules(Set<MembershipRules> membershipRules) {
		this.membershipRules = membershipRules;
	}

}
