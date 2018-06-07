package org.crossfit.app.domain.workouts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "WOD_RESULT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WodResult {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@ManyToOne(optional = false, cascade = {})
	private Wod wod;
	
	@JsonIgnore
	@ManyToOne(optional = false, cascade = {})
	private Member member;
	
	@Column(name = "date", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	public LocalDate date;

    @Column(name = "total_load_in_kilo")
	private double totalLoadInKilo;

    @Column(name = "total_minute")
	private int totalMinute;
    @Column(name = "total_second")
	private int totalSecond;

    @Column(name = "total_complete_round")
	private int totalCompleteRound;
    @Column(name = "total_reps")
	private int totalReps;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Wod getWod() {
		return wod;
	}
	public void setWod(Wod wod) {
		this.wod = wod;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public double getTotalLoadInKilo() {
		return totalLoadInKilo;
	}
	public void setTotalLoadInKilo(double totalLoadInKilo) {
		this.totalLoadInKilo = totalLoadInKilo;
	}
	public int getTotalMinute() {
		return totalMinute;
	}
	public void setTotalMinute(int totalMinute) {
		this.totalMinute = totalMinute;
	}
	public int getTotalSecond() {
		return totalSecond;
	}
	public void setTotalSecond(int totalSecond) {
		this.totalSecond = totalSecond;
	}
	public int getTotalCompleteRound() {
		return totalCompleteRound;
	}
	public void setTotalCompleteRound(int totalCompleteRound) {
		this.totalCompleteRound = totalCompleteRound;
	}
	public int getTotalReps() {
		return totalReps;
	}
	public void setTotalReps(int totalReps) {
		this.totalReps = totalReps;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WodResult other = (WodResult) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
}