package org.crossfit.app.domain.workouts.result;

public enum ResultDivision {
	WOMEN(18,35),
	MEN(18,35),
	TEAM(0,99);
	
	private final int ageMin;
	private final int ageMax;
	
	private ResultDivision(int min, int max) {
		this.ageMin = min;
		this.ageMax = max;
	}
	
}
