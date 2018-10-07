package org.crossfit.app.domain.workouts.enumeration;

import java.util.Comparator;
import java.util.function.Function;

import org.crossfit.app.domain.workouts.result.WodResult;

public enum WodScore {

	
	FOR_TIME(WodResult.COMPARE_FOR_TIME, WodResult.RESULT_FORMAT_FOR_TIME), 
	FOR_ROUNDS_REPS(WodResult.COMPARE_FOR_ROUND, WodResult.RESULT_FORMAT_FOR_ROUND), 
	FOR_LOAD(WodResult.COMPARE_FOR_LOAD, WodResult.RESULT_FORMAT_FOR_LOAD);
	
	private final Comparator<WodResult> comparator;
	private final Function<WodResult, String> resultMapper;
	
	WodScore(Comparator<WodResult> comparator,  Function<WodResult, String> resultMapper){
		this.comparator = comparator;
		this.resultMapper = resultMapper;
	}

	public Comparator<WodResult> getComparator() {
		return comparator;
	}

	public Function<WodResult, String> getResultMapper() {
		return resultMapper;
	}

}
