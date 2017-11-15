package org.crossfit.app.domain.enumeration;

import java.util.Arrays;
import java.util.List;

public enum MembershipRulesType {
	SUM, SUM_PER_WEEK, SUM_PER_4_WEEKS, SUM_PER_MONTH;
	
	public static final List<MembershipRulesType> BY_MONTH = Arrays.asList(SUM_PER_WEEK, SUM_PER_4_WEEKS, SUM_PER_MONTH);
}
