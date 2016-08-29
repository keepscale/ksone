package org.crossfit.app.web.rest.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @NotNull        
    private String price;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
