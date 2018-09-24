package org.crossfit.app.service;

import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.BookingEvent;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.exception.booking.NotBookingOwnerException;
import org.crossfit.app.exception.booking.UnableToDeleteBooking;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.CardEventRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.util.BookingRulesChecker;
import org.crossfit.app.web.rest.dto.BookingEventDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.bus.Event;
import reactor.bus.EventBus;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class BookingService {

    private final Logger log = LoggerFactory.getLogger(BookingService.class);

	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private CardEventRepository cardEventRepository;

    @Inject
    private CrossFitBoxSerivce boxService;
    
    @Inject
    private TimeService timeService;
    
    @Inject
	private SubscriptionRepository subscriptionRepository;

    @Inject
	private NotificationService notificationService;
    
    @Inject
	private EventBus eventBus;

    @Autowired
    private SimpMessagingTemplate template;

    @Transactional
	public void deleteBooking(Long id) throws NotBookingOwnerException, UnableToDeleteBooking{
        log.debug("Try to delete Booking : {}", id);
		Booking booking = bookingRepository.findById(id).get();

		CrossFitBox currentBox = boxService.findCurrentCrossFitBox();
		DateTime now = timeService.nowAsDateTime(currentBox);

    	if (!SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)){
    		if(booking == null || !booking.getSubscription().getMember().equals( SecurityUtils.getCurrentMember())){
    			throw new NotBookingOwnerException(booking, SecurityUtils.getCurrentMember());
    		}

    		
		
			Subscription bookingSubscription = subscriptionRepository.findOneWithRules(booking.getSubscription().getId());
			BookingRulesChecker checker = new BookingRulesChecker(now);
			Optional<MembershipRules> breakingRule = checker.breakRulesToCancel(booking, bookingSubscription.getMembership().getMembershipRules());
			
			if (breakingRule.isPresent()){
				throw new UnableToDeleteBooking(breakingRule.get());
			}
    		
    	}
    	
		cardEventRepository.deleteByBooking(booking);
        bookingRepository.deleteById(id);
        
    	BookingEvent bookingEvent = BookingEvent.deletedBooking(now, SecurityUtils.getCurrentMember(), booking, currentBox);
    	
    	notificationService.accept(bookingEvent);
    	
		eventBus.notify("bookings", Event.wrap(bookingEvent));
        template.convertAndSend("/topic/bookings", BookingEventDTO.mapper.apply(bookingEvent));
        
	}

	public Booking createBooking(Booking b, DateTime now) {
		CrossFitBox currentBox = boxService.findCurrentCrossFitBox();
		b.setBox(currentBox);
        Booking result = bookingRepository.save(b);
        BookingEvent bookingEvent = BookingEvent.createdBooking(now, SecurityUtils.getCurrentMember(), result, currentBox);

    	notificationService.accept(bookingEvent);
    	
		eventBus.notify("bookings", Event.wrap(bookingEvent));
        template.convertAndSend("/topic/bookings", BookingEventDTO.mapper.apply(bookingEvent));
		log.debug("Booking sauvegarde: {}", result);
		return result;
	}


}
