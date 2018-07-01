package org.crossfit.app.domain.workouts;

import java.util.Comparator;
import java.util.function.Function;

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

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.enumeration.Title;
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

	public static final Comparator<WodResult> COMPARE_FOR_TIME = Comparator.comparing(WodResult::getTotalMinute, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(WodResult::getTotalSecond, Comparator.nullsLast(Comparator.naturalOrder()));
	public static final Comparator<WodResult> COMPARE_FOR_ROUND = Comparator.comparing(WodResult::getTotalCompleteRound, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(WodResult::getTotalReps, Comparator.nullsLast(Comparator.naturalOrder()));
	public static final Comparator<WodResult> COMPARE_FOR_LOAD = Comparator.comparing(WodResult::getTotalLoadInKilo, Comparator.nullsLast(Comparator.naturalOrder()));

	public static final Function<WodResult, String> RESULT_FORMAT_FOR_TIME = r->r.getTotalMinute()+":"+r.getTotalSecond();
	public static final Function<WodResult, String> RESULT_FORMAT_FOR_ROUND = r->r.getTotalCompleteRound()+" ("+r.getTotalReps()+")";
	public static final Function<WodResult, String> RESULT_FORMAT_FOR_LOAD = r->r.getTotalLoadInKilo()+"";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@ManyToOne(optional = false, cascade = {})
	private Wod wod;
	
	@JsonIgnore
	@ManyToOne(optional = false, cascade = {})
	private Member member;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private ResultCategory category;
	
	@Column(name = "date", nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	public LocalDate date;

    @Column(name = "total_load_in_kilo")
	private Double totalLoadInKilo;

    @Column(name = "total_minute")
	private Integer totalMinute;
    @Column(name = "total_second")
	private Integer totalSecond;

    @Column(name = "total_complete_round")
	private Integer totalCompleteRound;
    @Column(name = "total_reps")
	private Integer totalReps;
    
    
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

	public Double getTotalLoadInKilo() {
		return totalLoadInKilo;
	}
	public void setTotalLoadInKilo(Double totalLoadInKilo) {
		this.totalLoadInKilo = totalLoadInKilo;
	}
	public Integer getTotalMinute() {
		return totalMinute;
	}
	public void setTotalMinute(Integer totalMinute) {
		this.totalMinute = totalMinute;
	}
	public Integer getTotalSecond() {
		return totalSecond;
	}
	public void setTotalSecond(Integer totalSecond) {
		this.totalSecond = totalSecond;
	}
	public Integer getTotalCompleteRound() {
		return totalCompleteRound;
	}
	public void setTotalCompleteRound(Integer totalCompleteRound) {
		this.totalCompleteRound = totalCompleteRound;
	}
	public Integer getTotalReps() {
		return totalReps;
	}
	public void setTotalReps(Integer totalReps) {
		this.totalReps = totalReps;
	}
	
	public ResultCategory getCategory() {
		return category;
	}
	public void setCategory(ResultCategory category) {
		this.category = category;
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