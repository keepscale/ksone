package org.crossfit.app.service;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TimeService {

	private final Logger log = LoggerFactory.getLogger(TimeService.class);

    @Inject
    private CrossFitBoxSerivce boxService;

    public List<String> getAvailableTimeZones(){
    	return Arrays.asList(TimeZone.getAvailableIDs());
    }
    
    public TimeZone getCurrentTimeZome(CrossFitBox box){
    	String boxTimeZoneID = box.getTimeZoneId();
    	TimeZone res = boxTimeZoneID != null ? TimeZone.getTimeZone(boxTimeZoneID) : DateTimeZone.UTC.toTimeZone();
    	log.info("Using TimeZone " + res.getDisplayName());
    	return res;
    }

    public DateTime nowAsDateTime(CrossFitBox box){
    	return DateTime.now(getDateTimeZone(box));
    }

	public DateTimeZone getDateTimeZone(CrossFitBox box) {
		return DateTimeZone.forTimeZone(getCurrentTimeZome(box));
	}
    public LocalDate nowAsLocalDate(CrossFitBox box){
    	return LocalDate.now(getDateTimeZone(box));
    }

	public DateTime parseDate(String pattern, String value, CrossFitBox box) {
		try {
			return DateTimeFormat.forPattern(pattern).withZone(getDateTimeZone(box)).parseDateTime(value);
		} catch (Exception e) {
			return null;
		}
	}
}
