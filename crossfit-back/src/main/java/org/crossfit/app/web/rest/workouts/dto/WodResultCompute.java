package org.crossfit.app.web.rest.workouts.dto;

import org.crossfit.app.domain.workouts.result.ResultCategory;
import org.crossfit.app.domain.workouts.result.ResultDivision;
import org.joda.time.LocalDate;

public class WodResultCompute implements Comparable<WodResultCompute>{

	private Long id;
	private ResultCategory category;
	private ResultDivision division;
	private Long memberId;
	private String displayName;
	private String displayResult;
	private Double score;
	private LocalDate date;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDisplayResult() {
		return displayResult;
	}
	public void setDisplayResult(String displayResult) {
		this.displayResult = displayResult;
	}
	public ResultCategory getCategory() {
		return category;
	}
	public void setCategory(ResultCategory category) {
		this.category = category;
	}
	public ResultDivision getDivision() {
		return division;
	}
	public void setDivision(ResultDivision division) {
		this.division = division;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	
	@Override
	public int compareTo(WodResultCompute o) {
		int c = this.category.compareTo(o.getCategory());
		c = (c==0) ? this.score.compareTo(o.getScore()) : c;
		return c;
	}
}
