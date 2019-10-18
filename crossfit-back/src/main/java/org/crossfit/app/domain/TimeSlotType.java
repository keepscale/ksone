package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.Objects;

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
@Table(name = "TIMESLOTTYPE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TimeSlotType extends AbstractAuditingEntity implements Serializable, Sortable {

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
    @Size(max = 512)     
    @Column(name = "description", nullable = false)
    private String description;
    
    @JsonIgnore
    @ManyToOne(optional=false)
    private CrossFitBox box;

    @NotNull
    @Column(name = "ordre", nullable = false)
    private Integer order = 0;

    @NotNull
    @Size(max = 64)     
    @Column(name = "web_css_class", nullable = false)
    private String webCssClass = "col-md-3 lexique";

    
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
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getWebCssClass() {
		return webCssClass;
	}

	public void setWebCssClass(String webCssClass) {
		this.webCssClass = webCssClass;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimeSlotType membershipType = (TimeSlotType) o;

        if ( ! Objects.equals(id, membershipType.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "TimeSlotType [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}
