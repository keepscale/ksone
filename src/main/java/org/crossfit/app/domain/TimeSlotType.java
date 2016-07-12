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


/**
 * A MembershipType.
 */
@Entity
@Table(name = "TIMESLOTTYPE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TimeSlotType extends AbstractAuditingEntity implements Serializable {

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
