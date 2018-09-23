package org.crossfit.app.event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.BookingEvent;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.enumeration.BookingEventType;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.TimeSlotNotificationRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.service.MailService;
import org.crossfit.app.service.TimeService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class BookingEventNotificationConsumer implements Consumer<Event<BookingEvent>> {

	private final Logger log = LoggerFactory.getLogger(BookingEventNotificationConsumer.class);

	@Inject
	private TimeSlotNotificationRepository notificationRepository;
	@Inject
	private MailService mailService;
    @Inject
    private BookingRepository bookingRepository;
    @Inject
    private TimeService timeService;

	public void accept(Event<BookingEvent> event) {
		Booking booking = event.getData().getBooking();
		CrossFitBox box = booking.getBox();
		
		Member bookingMember = booking.getSubscription().getMember();
		DateTime bookingStartAt = booking.getStartAt().withZone(timeService.getDateTimeZone(box));
		DateTime bookingEndAt = booking.getEndAt().withZone(timeService.getDateTimeZone(box));
		
		LocalTime bookingStartTime = bookingStartAt.toLocalTime();
		LocalTime bookingEndTime = bookingEndAt.toLocalTime();
		TimeSlotType bookingTimeSlotType = booking.getTimeSlotType();

		log.debug("{} {}, on cherche le timeslot correspondant. start:{}, end:{}, timeSlotTypeId:{}, boxId:{}", event.getData().getEventType(), booking, bookingStartTime, bookingEndTime, bookingTimeSlotType.getId(), box.getId());

		LocalDate bookingStartDate = bookingStartAt.toLocalDate();
		Set<TimeSlotNotification> notifAtBookingDate = notificationRepository.findAll(bookingStartDate, bookingStartTime, bookingEndTime, bookingTimeSlotType);

		log.info("{} {}, recherche des demandes de notif pour le {} => {} demandes de notifs", event.getData().getEventType(), booking, bookingStartDate, notifAtBookingDate.size());
		
		if (notifAtBookingDate.isEmpty()) {
			return;
		}

		TimeSlot timeSlot = notifAtBookingDate.iterator().next().getTimeSlot();
		
		
		if (event.getData().getEventType() == BookingEventType.DELETED) {
			long bookingCount = bookingRepository.findAllAt(box, bookingStartAt, bookingEndAt).stream().filter(b->b.getTimeSlotType().equals(bookingTimeSlotType)).count();
			if (bookingCount < timeSlot.getMaxAttendees()){
				log.info("Suppression d'une résa, on notifie toutes les personnes en attente: {}", notifAtBookingDate);
				notificationRepository.deleteAll(notifAtBookingDate);
				notificationRepository.flush();
				mailService.sendNotification(notifAtBookingDate);	
			}
			else{
				log.info("Une resa a ete supprimée, mais le nombre d'inscrit est encore au dessus: {} >= {}", bookingCount, timeSlot.getMaxAttendees());
			}
		}
		else if (event.getData().getEventType() == BookingEventType.CREATED) {
			List<TimeSlotNotification> notifEnAttenteDuMembreDeLaResa = notifAtBookingDate.stream().filter(notif->notif.getMember().equals(bookingMember)).collect(Collectors.toList());
			if (!notifEnAttenteDuMembreDeLaResa.isEmpty()) {
				log.info("Creation d'un résa pour {}, on supprime ses demandes de notif: {}", bookingMember, notifEnAttenteDuMembreDeLaResa);
				notificationRepository.deleteAll(notifEnAttenteDuMembreDeLaResa);				
			}
		}
	}

}