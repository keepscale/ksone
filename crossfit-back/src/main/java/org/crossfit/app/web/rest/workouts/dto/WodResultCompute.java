package org.crossfit.app.web.rest.workouts.dto;

import org.crossfit.app.domain.enumeration.Title;
import org.crossfit.app.domain.workouts.ResultCategory;

public class WodResultCompute {

	private Integer order;
	private Long id;
	private Title title;
	private ResultCategory category;
	private String displayName;
	private String displayResult;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
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
	public Title getTitle() {
		return title;
	}
	public void setTitle(Title title) {
		this.title = title;
	}
	public ResultCategory getCategory() {
		return category;
	}
	public void setCategory(ResultCategory category) {
		this.category = category;
	}
	
}
