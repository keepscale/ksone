package org.crossfit.app.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A CrossFitBox.
 */
@Entity
@Table(name = "CROSSFITBOX")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CrossFitBox extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    

    @NotNull        
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull        
    @Column(name = "website", nullable = false)
    private String website;

    @NotNull        
    @Column(name = "adminwebsite", nullable = false)
    private String adminwebsite;

    @NotNull        
    @Column(name = "bookingwebsite", nullable = false)
    private String bookingwebsite;

    @NotNull        
    @Column(name = "rootwebsite", nullable = false)
    private String rootwebsite;

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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAdminwebsite() {
        return adminwebsite;
    }

    public void setAdminwebsite(String adminwebsite) {
        this.adminwebsite = adminwebsite;
    }

    public String getBookingwebsite() {
        return bookingwebsite;
    }

    public void setBookingwebsite(String bookingwebsite) {
        this.bookingwebsite = bookingwebsite;
    }

    public String getRootwebsite() {
        return rootwebsite;
    }

    public void setRootwebsite(String rootwebsite) {
        this.rootwebsite = rootwebsite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CrossFitBox crossFitBox = (CrossFitBox) o;

        if ( ! Objects.equals(id, crossFitBox.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CrossFitBox{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", website='" + website + "'" +
                ", adminwebsite='" + adminwebsite + "'" +
                ", bookingwebsite='" + bookingwebsite + "'" +
                ", rootwebsite='" + rootwebsite + "'" +
                '}';
    }
}
