package org.crossfit.app.web.rest.dto.planning;

import java.util.List;

public class PlanningDTO {
	
	private int page;
	
	private final List<PlanningDayDTO> days;

	public PlanningDTO(int page, List<PlanningDayDTO> days) {
		super();
		this.page = page;
		this.days = days;
	}

	public List<PlanningDayDTO> getDays() {
		return days;
	}

	public int getPage() {
		return page;
	}

}