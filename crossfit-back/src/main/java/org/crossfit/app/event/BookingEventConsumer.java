package org.crossfit.app.event;

import javax.inject.Inject;

import org.crossfit.app.domain.BookingEvent;
import org.crossfit.app.repository.BookingEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class BookingEventConsumer implements Consumer<Event<BookingEvent>> {

	private final Logger log = LoggerFactory.getLogger(BookingEventConsumer.class);

	@Inject
	private BookingEventRepository bookingEventRepository;

	public void accept(Event<BookingEvent> event) {
		BookingEvent bookingEvent = event.getData();
		log.info("Booking {}: {}", bookingEvent.getEventType(), bookingEvent);
		bookingEventRepository.save(bookingEvent);

	}

}