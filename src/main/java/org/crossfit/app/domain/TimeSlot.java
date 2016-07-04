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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.enumeration.Level;
import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.CustomLocalTimeDeserializer;
import org.crossfit.app.domain.util.CustomLocalTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A TimeSlot.
 */
@Entity
@Table(name = "TIMESLOT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TimeSlot extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "name")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrent", nullable = false)
    private TimeSlotRecurrent recurrent;
    
    @Min(value = 1)
    @Max(value = 7)        
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;
          
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "date", nullable = true)
    private DateTime date;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalTimeAsTimestamp")
    @JsonSerialize(using = CustomLocalTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalTimeAsTimestamp")
    @JsonSerialize(using = CustomLocalTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Min(value = 0)        
    @Column(name = "max_attendees")
    private Integer maxAttendees;

    @NotNull        
    @Enumerated(EnumType.STRING)
    @Column(name = "required_level", nullable = false)
    private Level requiredLevel;

    @ManyToOne
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

	public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Level getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(Level requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public CrossFitBox getBox() {
        return box;
    }

    public void setBox(CrossFitBox crossFitBox) {
        this.box = crossFitBox;
    }
    

    public TimeSlotRecurrent getRecurrent() {
		return recurrent;
	}

	public void setRecurrent(TimeSlotRecurrent recurrent) {
		this.recurrent = recurrent;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimeSlot timeSlot = (TimeSlot) o;

        if ( ! Objects.equals(id, timeSlot.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "id=" + id +
                ", dayOfWeek='" + dayOfWeek + "'" +
                ", startTime='" + startTime + "'" +
                ", endTime='" + endTime + "'" +
                ", maxAttendees='" + maxAttendees + "'" +
                ", requiredLevel='" + requiredLevel + "'" +
                '}';
    }
    
    public DateTime getStartDateTime(DateTime firstDateOfWeek){
    	DateTime slotDateDay = firstDateOfWeek.dayOfWeek().setCopy(getDayOfWeek());
		DateTime startDateTime = slotDateDay.withTime(getStartTime().getHourOfDay(), getStartTime().getMinuteOfHour(), 0, 0);
		return startDateTime;
    }

    public DateTime getEndDateTime(DateTime firstDateOfWeek){
    	DateTime slotDateDay = firstDateOfWeek.dayOfWeek().setCopy(getDayOfWeek());
		
		DateTime endDateTime = slotDateDay.withTime(getEndTime().getHourOfDay(), getEndTime().getMinuteOfHour(), 0, 0);
		return endDateTime;
    }
}
