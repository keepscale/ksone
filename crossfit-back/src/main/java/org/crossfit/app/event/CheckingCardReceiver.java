package org.crossfit.app.event;

import javax.inject.Inject;

import org.crossfit.app.domain.CardEvent;
import org.crossfit.app.repository.CardEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.fn.Consumer;

@Service
public class CheckingCardReceiver implements Consumer<Event<CardEvent>> {

	private final Logger log = LoggerFactory.getLogger(CheckingCardReceiver.class);

	@Inject
	private CardEventRepository cardEventRepository;

	public void accept(Event<CardEvent> event) {
		CardEvent cardEvent = event.getData();
		log.info("Une carte a ete scanne: {}", cardEvent);
		cardEventRepository.save(cardEvent);
	}

}