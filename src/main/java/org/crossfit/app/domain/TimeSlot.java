package org.crossfit.app.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.CustomLocalTimeDeserializer;
import org.crossfit.app.domain.util.CustomLocalTimeSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    
    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalTimeAsTimestamp")
    @JsonSerialize(using = CustomLocalTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalTimeAsTimestamp")
    @JsonSerialize(using = CustomLocalTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalTimeDeserializer.class)
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Min(value = 0)        
    @Column(name = "max_attendees")
    private Integer maxAttendees;

    @NotNull
    @ManyToOne
    private TimeSlotType timeSlotType;

    @JsonIgnore
    @ManyToOne
    private CrossFitBox box;
    

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "visible_after", nullable = true)
    private LocalDate visibleAfter;


    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "visible_before", nullable = false)
    private LocalDate visibleBefore;
       
    @Transient
    private List<TimeSlotExclusion> exclusions = new ArrayList<TimeSlotExclusion>();
    

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

	public TimeSlotType getTimeSlotType() {
		return timeSlotType;
	}

	public void setTimeSlotType(TimeSlotType timeSlotType) {
		this.timeSlotType = timeSlotType;
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
	
	public LocalDate getVisibleAfter() {
		return visibleAfter;
	}

	public void setVisibleAfter(LocalDate visibleAfter) {
		this.visibleAfter = visibleAfter;
	}

	public LocalDate getVisibleBefore() {
		return visibleBefore;
	}

	public void setVisibleBefore(LocalDate visibleBefore) {
		this.visibleBefore = visibleBefore;
	}
	
	public List<TimeSlotExclusion> getExclusions() {
		return exclusions;
	}

	public void setExclusions(List<TimeSlotExclusion> exclusions) {
		this.exclusions = exclusions;
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
		return "TimeSlot [id=" + id + ", name=" + name + ", recurrent=" + recurrent + ", dayOfWeek=" + dayOfWeek
				+ ", date=" + date + ", startTime=" + startTime + ", endTime=" + endTime + ", maxAttendees="
				+ maxAttendees + ", visibleAfter=" + visibleAfter + ", visibleBefore=" + visibleBefore + "]";
	}

    
}
