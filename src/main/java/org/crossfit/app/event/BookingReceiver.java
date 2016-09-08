package org.crossfit.app.event;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class BookingReceiver implements Consumer<Event<Booking>> {

	private final Logger log = LoggerFactory.getLogger(BookingReceiver.class);

	@Inject
	private BookingRepository bookingRepository;

	public void accept(Event<Booking> event) {
		log.info("Recherche de la resa");
		bookingRepository.findAll();
		log.info("Suppression de la resa " + event.getData());
	}

}