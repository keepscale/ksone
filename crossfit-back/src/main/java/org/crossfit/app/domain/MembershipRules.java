package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.enumeration.MembershipRulesType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * A MembershipType.
 */
@Entity
@Table(name = "MEMBERSHIP_RULES")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MembershipRules implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
          
    @NotNull
    @Max(value = 20)        
    @Column(name = "number_of_session", nullable = false)
    private Integer numberOfSession;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MembershipRulesType type;

    @ManyToMany(fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "MEMBERSHIP_RULES_TIMESLOTTYPE",
               joinColumns = @JoinColumn(name="membership_rules_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="timeslottype_id", referencedColumnName="ID"))
    private Set<TimeSlotType> applyForTimeSlotTypes = new HashSet<>();

    @Column(name = "nb_max_booking", nullable = false)
    private int nbMaxBooking = 3;
    
    @Column(name = "nb_max_day_booking", nullable = false)
    private int nbMaxDayBooking = 14;

    @Column(name = "nb_hours_at_least_to_book", nullable = false)
    private int nbHoursAtLeastToBook = 6;
    
    @Column(name = "nb_hours_at_least_to_cancel", nullable = false)
    private int nbHoursAtLeastToCancel = 6;

    @Column(name = "must_have_med_cert", nullable = false)
    private boolean mustHaveMedicalCertificate = false;

    @Column(name = "med_cert_valid_for_less_than_nb_years", nullable = false)
    private int medicalCertificateValidForLessThanNbYears = 1;
    
    @JsonIgnore
    @ManyToOne(optional=false)
    @JoinColumn(name="membership_id")
    private Membership membership;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumberOfSession() {
        return numberOfSession;
    }

    public void setNumberOfSession(Integer numberOfSession) {
        this.numberOfSession = numberOfSession;
    }

    public MembershipRulesType getType() {
		return type;
	}

	public void setType(MembershipRulesType type) {
		this.type = type;
	}

	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

    public Set<TimeSlotType> getApplyForTimeSlotTypes() {
		return applyForTimeSlotTypes;
	}

	public void setApplyForTimeSlotTypes(Set<TimeSlotType> applyForTimeSlotTypes) {
		this.applyForTimeSlotTypes = applyForTimeSlotTypes;
	}

	public int getNbMaxDayBooking() {
		return nbMaxDayBooking;
	}

	public void setNbMaxDayBooking(int nbMaxDayBooking) {
		this.nbMaxDayBooking = nbMaxDayBooking;
	}

	public int getNbHoursAtLeastToBook() {
		return nbHoursAtLeastToBook;
	}

	public void setNbHoursAtLeastToBook(int nbHoursAtLeastToBook) {
		this.nbHoursAtLeastToBook = nbHoursAtLeastToBook;
	}

	public int getNbHoursAtLeastToCancel() {
		return nbHoursAtLeastToCancel;
	}

	public void setNbHoursAtLeastToCancel(int nbHoursAtLeastToCancel) {
		this.nbHoursAtLeastToCancel = nbHoursAtLeastToCancel;
	}

	public int getNbMaxBooking() {
		return nbMaxBooking;
	}

	public void setNbMaxBooking(int nbMaxBooking) {
		this.nbMaxBooking = nbMaxBooking;
	}

	public boolean isMustHaveMedicalCertificate() {
		return mustHaveMedicalCertificate;
	}

	public void setMustHaveMedicalCertificate(boolean mustHaveMedicalCertificate) {
		this.mustHaveMedicalCertificate = mustHaveMedicalCertificate;
	}

	public int getMedicalCertificateValidForLessThanNbYears() {
		return medicalCertificateValidForLessThanNbYears;
	}

	public void setMedicalCertificateValidForLessThanNbYears(int medicalCertificateValidForLessThanNbYears) {
		this.medicalCertificateValidForLessThanNbYears = medicalCertificateValidForLessThanNbYears;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MembershipRules membershipType = (MembershipRules) o;

        if ( ! Objects.equals(id, membershipType.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MembershipRules{" +
                "id=" + id +
                ", numberOfSession='" + numberOfSession + "'" +
                ", type='" + type + "'" +
                '}';
    }
}
