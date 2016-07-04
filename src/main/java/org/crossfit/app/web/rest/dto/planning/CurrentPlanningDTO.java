package org.crossfit.app.web.rest.dto.planning;

import java.util.List;

public class CurrentPlanningDTO  {
	private final List<CurrentPlanningDayDTO> days;

	public CurrentPlanningDTO(List<CurrentPlanningDayDTO> days) {
		this.days = days;
	}

	public List<CurrentPlanningDayDTO> getDays() {
		return days;
	}

}