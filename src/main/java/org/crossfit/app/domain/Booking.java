package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A Booking.
 */
@Entity
@Table(name = "BOOKING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Booking extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @NotNull        
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @ManyToOne
    private Subscription subscription;

    @ManyToOne
    private CrossFitBox box;

    @ManyToOne
    private TimeSlotType timeSlotType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    
    public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public CrossFitBox getBox() {
		return box;
	}

	public void setBox(CrossFitBox box) {
		this.box = box;
	}

	public TimeSlotType getTimeSlotType() {
		return timeSlotType;
	}

	public void setTimeSlotType(TimeSlotType timeSlotType) {
		this.timeSlotType = timeSlotType;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Booking booking = (Booking) o;

        if ( ! Objects.equals(id, booking.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", startAt='" + startAt + "'" +
                ", endAt='" + endAt + "'" +
                ", status='" + status + "'" +
                ", createdDate='" + getCreatedDate() + "'" +
                ", createdBy='" + getCreatedBy() + "'" +
                '}';
    }
}
