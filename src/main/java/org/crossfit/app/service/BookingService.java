package org.crossfit.app.service;

import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private EventBus eventBus;

    @Transactional
	public void deleteBooking(Long id, boolean notifyEventBus) throws NotBookingOwnerException, UnableToDeleteBooking{
        log.debug("Try to delete Booking : {}", id);
		Booking booking = bookingRepository.findOne(id);
		

    	if (!SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)){
    		if(booking == null || !booking.getSubscription().getMember().equals( SecurityUtils.getCurrentMember())){
    			throw new NotBookingOwnerException(booking, SecurityUtils.getCurrentMember());
    		}

    		CrossFitBox currentBox = boxService.findCurrentCrossFitBox();
			DateTime now = timeService.nowAsDateTime(currentBox);
    		
		
			Subscription bookingSubscription = subscriptionRepository.findOneWithRules(booking.getSubscription().getId());
			BookingRulesChecker checker = new BookingRulesChecker(now);
			Optional<MembershipRules> breakingRule = checker.breakRulesToCancel(booking, bookingSubscription.getMembership().getMembershipRules());
			
			if (breakingRule.isPresent()){
				throw new UnableToDeleteBooking(breakingRule.get());
			}
    		
    	}
    	
		cardEventRepository.deleteByBooking(booking);
        bookingRepository.delete(id);
        
        if (notifyEventBus){
        	eventBus.notify("booking", Event.wrap(booking));
        }
	}


}
