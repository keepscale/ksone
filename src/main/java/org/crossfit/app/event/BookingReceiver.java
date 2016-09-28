package org.crossfit.app.event;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.TimeSlotNotificationRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.service.MailService;
import org.crossfit.app.service.TimeService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class BookingReceiver implements Consumer<Event<Booking>> {

	private final Logger log = LoggerFactory.getLogger(BookingReceiver.class);

	@Inject
	private TimeSlotNotificationRepository notificationRepository;
	@Inject
	private TimeSlotRepository timeSlotRepository;
	@Inject
	private MailService mailService;
    @Inject
    private BookingRepository bookingRepository;
    @Inject
    private TimeService timeService;

	public void accept(Event<Booking> event) {
		Booking booking = event.getData();
		CrossFitBox box = booking.getBox();
		
		
		DateTime bookingStartAt = booking.getStartAt().withZone(timeService.getDateTimeZone(box));
		DateTime bookingEndAt = booking.getEndAt().withZone(timeService.getDateTimeZone(box));
		
		LocalTime start = bookingStartAt.toLocalTime();
		LocalTime end = bookingEndAt.toLocalTime();
		TimeSlotType timeSlotType = booking.getTimeSlotType();

		log.debug("Suppression d'une resa {}, on cherche le timeslot correspondant. start:{}, end:{}, timeSlotTypeId:{}, boxId:{}", booking, start, end, timeSlotType.getId(), box.getId());

		LocalDate localDate = bookingStartAt.toLocalDate();
		log.debug("Recherche des demandes de notif pour le {}", localDate);
		List<TimeSlotNotification> notifAtBookingDate = notificationRepository.findAllByDate(localDate);
		
		List<Long> timeSlotIds = notifAtBookingDate.stream().map(TimeSlotNotification::getTimeSlot).map(TimeSlot::getId).collect(Collectors.toList());
		List<TimeSlot> possibleTimeSlotMatch = timeSlotRepository.findAll(timeSlotIds);

		Optional<TimeSlot> optTimeSlot = possibleTimeSlotMatch.stream().filter(t->{
	    	return t.getTimeSlotType().equals(timeSlotType) && t.getStartTime().equals(start) && t.getEndTime().equals(end);
		}).findFirst();
		
		if (optTimeSlot.isPresent()){

			List<TimeSlotNotification> notifs = notifAtBookingDate.stream().filter(t->t.getTimeSlot().equals(optTimeSlot.get())).collect(Collectors.toList());
			
			if (notifs != null && !notifs.isEmpty()){
				log.debug("Recherche des resa au {} -> {}", bookingStartAt, bookingEndAt);
				
				int bookingCount = bookingRepository.findAllAt(box, bookingStartAt, bookingEndAt).size();
				if (bookingCount < optTimeSlot.get().getMaxAttendees()){
					mailService.sendNotification(notifs);	
					notificationRepository.delete(notifs);
				}
				else{
					log.info("Une resa a ete supprimée, mais le nombre d'inscrit est encore au dessus: {} >= {}", bookingCount, optTimeSlot.get().getMaxAttendees());
				}
			}		
		}
		else{
			log.debug("Impossible de trouver un timeslot matchant la resa.");
		}
		
	}

}