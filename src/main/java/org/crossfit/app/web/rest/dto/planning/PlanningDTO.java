package org.crossfit.app.web.rest.dto.planning;

import java.util.List;

public class PlanningDTO {
	private final List<PlanningDayDTO> days;

	public PlanningDTO(List<PlanningDayDTO> days) {
		super();
		this.days = days;
	}

	public List<PlanningDayDTO> getDays() {
		return days;
	}

}