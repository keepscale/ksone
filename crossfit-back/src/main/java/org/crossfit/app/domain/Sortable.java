package org.crossfit.app.domain;

import java.util.Comparator;

public interface Sortable extends Comparable<Sortable>{

	public Integer getOrder();
	public String getName();
	
	public static final Comparator<Sortable> COMPARATOR = 
			Comparator.comparing(Sortable::getOrder).thenComparing(Comparator.comparing(Sortable::getName));

	@Override
	default int compareTo(Sortable o) {
		return COMPARATOR.compare(this, o);
	}
	
	
}
