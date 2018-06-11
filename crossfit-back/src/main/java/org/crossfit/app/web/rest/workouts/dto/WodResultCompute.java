package org.crossfit.app.web.rest.workouts.dto;

public class WodResultCompute {

	private Integer order;
	private Long id;
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
}
