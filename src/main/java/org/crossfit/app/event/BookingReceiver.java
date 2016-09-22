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
import org.crossfit.app.repository.TimeSlotNotificationRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.service.MailService;
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

	public void accept(Event<Booking> event) {
		Booking booking = event.getData();
		LocalTime start = booking.getStartAt().toLocalTime();
		LocalTime end = booking.getEndAt().toLocalTime();
		TimeSlotType timeSlotType = booking.getTimeSlotType();
		CrossFitBox box = booking.getBox();

		log.debug("Suppression d'une resa, on cherche le timeslot correspondant. start:{}, end:{}, timeSlotTypeId:{}, boxId:{}", start, end, timeSlotType.getId(), box.getId());

		List<TimeSlotNotification> notifAtBookingDate = notificationRepository.findAllByDate(booking.getStartAt().toLocalDate());
		
		List<TimeSlot> possibleTimeSlotMatch = timeSlotRepository.findAll(notifAtBookingDate.stream().map(TimeSlotNotification::getTimeSlot).map(TimeSlot::getId).collect(Collectors.toList()));

		Optional<TimeSlot> optTimeSlot = possibleTimeSlotMatch.stream().filter(t->{
	    	return t.getTimeSlotType().equals(timeSlotType) && t.getStartTime().equals(start) && t.getEndTime().equals(end);
		}).findFirst();
		
		if (optTimeSlot.isPresent()){

			List<TimeSlotNotification> notifs = notifAtBookingDate.stream().filter(t->t.getTimeSlot().equals(optTimeSlot.get())).collect(Collectors.toList());
			
			mailService.sendNotification(notifs);			
			notificationRepository.delete(notifs);
		}
		else{
			log.debug("Impossible de trouver un timeslot matchant la resa.");
		}
		
	}

}