package org.crossfit.app.web.rest.ical;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.TimeService;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.HostInfo;
import net.fortuna.ical4j.util.SimpleHostInfo;
import net.fortuna.ical4j.util.UidGenerator;

/**
 * REST controller for managing ical booking event.
 */
@RestController
@RequestMapping("/public")
public class ICalendarBookingResource {

    private final Logger log = LoggerFactory.getLogger(ICalendarBookingResource.class);

    @Inject
    private BookingRepository bookingRepository;
    
    @Inject
    private MemberService memberService;

	@Inject
	private CrossFitBoxSerivce boxService;
	@Inject
	private TimeService timeService;


    /**
     * GET  /bookings -> get all the bookings to ICS
     */
    @RequestMapping(value = "/ical/{uuid}/booking.ics",
            method = RequestMethod.GET,
            produces = "text/calendar; charset=UTF-8")
    public ResponseEntity<String> getAllToICalendar(
    		@PathVariable String uuid) throws URISyntaxException {
    	
    	
    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
    	DateTimeZone dateTimeZone = timeService.getDateTimeZone(currentCrossFitBox);
    	
    	Optional<Member> optMember = memberService.findMemberByUuid(uuid);
    	
    	if (!optMember.isPresent()){
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	Member member = optMember.get();

    	log.debug("Accessing ical from uuid {} / {}", uuid, member.getLogin());
    	
		List<Booking> bookings = bookingRepository.findAllByMember(optMember.get());

    	Calendar calendar = new Calendar();
    	calendar.getProperties().add(new ProdId("-//Loic Gangloff//"+currentCrossFitBox.getName()+"//iCal4j 2.0.0//EN"));
    	calendar.getProperties().add(Version.VERSION_2_0);
    	calendar.getProperties().add(CalScale.GREGORIAN);
    	calendar.getProperties().add(new Name("Réservation de " + member.getFirstName() + " " + member.getLastName() + " à " + currentCrossFitBox.getName()));
    	
    	TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
    	TimeZone timeZone = registry.getTimeZone(currentCrossFitBox.getTimeZoneId());
		VTimeZone vtz = timeZone.getVTimeZone();
    	
    	for (Booking booking : bookings) {

    		net.fortuna.ical4j.model.DateTime start = 
    				new net.fortuna.ical4j.model.DateTime(booking.getStartAt().toDateTime(DateTimeZone.UTC).getMillis());
			net.fortuna.ical4j.model.DateTime end = 
					new net.fortuna.ical4j.model.DateTime(booking.getEndAt().toDateTime(DateTimeZone.UTC).getMillis());
			//start.setTimeZone(timeZone);
			//end.setTimeZone(timeZone);
			
			VEvent event = new VEvent(start, end, booking.getTimeSlotType().getName());

    		HostInfo hostinfo = new SimpleHostInfo(currentCrossFitBox.getBookingwebsite());
			UidGenerator ug = new UidGenerator(hostinfo, String.valueOf(booking.getId()));
			event.getProperties().add(ug.generateUid());
			event.getProperties().add(vtz.getTimeZoneId());
    		
			calendar.getComponents().add(event);
		}

    	return ResponseEntity.ok(calendar.toString());
    }
    

}
