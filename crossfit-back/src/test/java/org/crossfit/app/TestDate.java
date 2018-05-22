package org.crossfit.app;

import java.time.ZoneId;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class TestDate {

	
	public static void main(String[] args) {
		LocalDate d = LocalDate.now();
		
		System.out.println(d);
		
		LocalTime time = new LocalTime(10, 30, 00);
		
		System.out.println(d.toDateTime(time));
		System.out.println(d.toDateTime(time, DateTimeZone.UTC));
		System.out.println(d.toDateTime(time, DateTimeZone.forTimeZone(TimeZone.getTimeZone("fr"))));
	}
}
