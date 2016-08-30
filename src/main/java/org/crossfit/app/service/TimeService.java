package org.crossfit.app.service;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TimeService {
    private static final DateTimeZone UTC = DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC"));

	private final Logger log = LoggerFactory.getLogger(TimeService.class);

    @Inject
    private CrossFitBoxSerivce boxService;

    public List<String> getAvailableTimeZones(){
    	return Arrays.asList(TimeZone.getAvailableIDs());
    }
    
    public TimeZone getCurrentTimeZome(){
    	String boxTimeZoneID = boxService.findCurrentCrossFitBox().getTimeZoneId();
    	TimeZone res = boxTimeZoneID != null ? TimeZone.getTimeZone(boxTimeZoneID) : UTC.toTimeZone();
    	log.debug("Using TimeZone " + res.getDisplayName());
    	return res;
    }

    public DateTime now(){
    	return DateTime.now(DateTimeZone.forTimeZone(getCurrentTimeZome()));
    }

	public DateTime parseDateAsUTC(String pattern, String value) {
		try {
			return DateTimeFormat.forPattern(pattern).withZone(UTC).parseDateTime(value);
		} catch (Exception e) {
			return null;
		}
	}
}
