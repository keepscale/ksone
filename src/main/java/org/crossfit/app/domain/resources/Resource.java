package org.crossfit.app.domain.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crossfit.app.domain.AbstractAuditingEntity;
import org.crossfit.app.domain.CrossFitBox;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * A Resource.
 */
@Entity
@Table(name = "RESOURCE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Resource extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull        
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotNull     
    @Size(max = 32)
    @Column(name = "color", nullable = false)
    private String color;
    
    @NotNull   
    @Size(max = 255)     
    @Column(name = "description", nullable = false)
    private String description;
    
    @JsonIgnore
    @ManyToOne(optional=false)
    private CrossFitBox box;
    
    @OneToMany(mappedBy="resource", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
    private List<ResourceMemberRules> rules = new ArrayList<>();

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public CrossFitBox getBox() {
        return box;
    }

    public void setBox(CrossFitBox crossFitBox) {
        this.box = crossFitBox;
    }

    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<ResourceMemberRules> getRules() {
		return rules;
	}

	public void setRules(List<ResourceMemberRules> rules) {
		this.rules = rules;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource res = (Resource) o;

        if ( ! Objects.equals(id, res.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "Resource [id=" + id + ", name=" + name + ", description=" + description + ", rules="+rules+"]";
	}

}
