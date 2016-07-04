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
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * A MembershipType.
 */
@Entity
@Table(name = "MEMBERSHIPTYPE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MembershipType extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    

    @NotNull        
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull        
    @Column(name = "price", nullable = false)
    private String price;
    
    @Column(name = "open_access")
    private Boolean openAccess;

    @Max(value = 100)        
    @Column(name = "number_of_session")
    private Integer numberOfSession;

    @NotNull
    @Max(value = 20)        
    @Column(name = "number_of_session_per_week", nullable = false)
    private Integer numberOfSessionPerWeek;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getOpenAccess() {
        return openAccess;
    }

    public void setOpenAccess(Boolean openAccess) {
        this.openAccess = openAccess;
    }

    public Integer getNumberOfSession() {
        return numberOfSession;
    }

    public void setNumberOfSession(Integer numberOfSession) {
        this.numberOfSession = numberOfSession;
    }

    public Integer getNumberOfSessionPerWeek() {
        return numberOfSessionPerWeek;
    }

    public void setNumberOfSessionPerWeek(Integer numberOfSessionPerWeek) {
        this.numberOfSessionPerWeek = numberOfSessionPerWeek;
    }

    public CrossFitBox getBox() {
        return box;
    }

    public void setBox(CrossFitBox crossFitBox) {
        this.box = crossFitBox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MembershipType membershipType = (MembershipType) o;

        if ( ! Objects.equals(id, membershipType.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MembershipType{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", price='" + price + "'" +
                ", openAccess='" + openAccess + "'" +
                ", numberOfSession='" + numberOfSession + "'" +
                ", numberOfSessionPerWeek='" + numberOfSessionPerWeek + "'" +
                '}';
    }
}
