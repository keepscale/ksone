package org.crossfit.app.service.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.MembershipRulesException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredForBookingException;
import org.crossfit.app.exception.rules.SubscriptionException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class BookingRulesChecker {

	private final Map<Subscription, List<Booking>> bookingsBySubscriptions;
	private final Set<Subscription> subscriptions;



	public BookingRulesChecker(List<Booking> bookings, Set<Subscription> subscriptions) {
		super();
		this.bookingsBySubscriptions = bookings.stream().collect(Collectors.groupingBy(Booking::getSubscription));
		this.subscriptions = new HashSet<>(subscriptions);
	}


	public Subscription findSubscription(Member owner, TimeSlotType timeSlotType, DateTime startAt) throws ManySubscriptionsAvailableException, NoSubscriptionAvailableException {
		//Verifier qu'au moins une de ses souscription lui permet de reserver ce slot
		
		Booking booking = new Booking();
        booking.setTimeSlotType(timeSlotType);
        booking.setStartAt(startAt);
                
        List<Subscription> possibleSubscriptionsToUse = new ArrayList<>();
        List<SubscriptionException> subscriptionsInvalid = new ArrayList<>();
         
		for (Subscription subscription : subscriptions) {
			
			try {
				if(subscription.getSubscriptionEndDate() != null && subscription.getSubscriptionEndDate().isBefore(LocalDate.now()))
					throw new SubscriptionDateExpiredException(subscription);

				if(subscription.getSubscriptionEndDate() != null && subscription.getSubscriptionEndDate().isBefore(startAt.toLocalDate()))
					throw new SubscriptionDateExpiredForBookingException(subscription, booking);

							
				//On ajoute à toute les resa passée, celle en cours de préparation
				List<Booking> subscriptionBookings = new ArrayList(bookingsBySubscriptions.get(subscription));		
				booking.setSubscription(subscription);
				subscriptionBookings.add(booking);
				
				//On récupère les regles de gestion associé à cet abonnement
				Set<MembershipRules> rules = subscription.getMembership().getMembershipRules();
				
				//On cherche les regles violées
				List<MembershipRules> breakingRules = rules.stream().filter(isRuleBreaking(subscriptionBookings)).collect(Collectors.toList());
				
				if (breakingRules.isEmpty()){ //Aucune regle violee ?
					possibleSubscriptionsToUse.add(subscription);
				}
				else{
					throw new MembershipRulesException(subscription, booking, breakingRules);
				}
			} catch (SubscriptionDateExpiredException
					| SubscriptionDateExpiredForBookingException
					| MembershipRulesException e) {				
				subscriptionsInvalid.add(e);
			}
		}
		
		if (possibleSubscriptionsToUse.isEmpty()){
			throw new NoSubscriptionAvailableException(subscriptionsInvalid);
		}
		else if (possibleSubscriptionsToUse.size() == 1){
			return possibleSubscriptionsToUse.get(0);
		}
		else{
			throw new ManySubscriptionsAvailableException(possibleSubscriptionsToUse);
		}
		
	}
    
	
	
	private Predicate<? super MembershipRules> isRuleBreaking(List<Booking> bookings) {
		final List<Booking> _bookings = bookings;

		return new Predicate<MembershipRules>() {

			@Override
			public boolean test(MembershipRules rule) {
				if(rule.getNumberOfSession() < 0) //illimité => Valid
					return false;
				
				//On cherche les résa a un type de creneau ou s'appplique la regle
				Stream<Booking> bookingsForRules = _bookings.stream().filter(b->rule.getApplyForTimeSlotTypes().contains(b.getTimeSlotType()));
				final DateTime max = DateTime.now();
				
				switch (rule.getType()) {
					case SUM: //somme total des resa
						break;

					case SUM_PER_WEEK: //somme total des resa de la semaine passe
						bookingsForRules = bookingsForRules.filter(b->b.getStartAt().isAfter(max.minusWeeks(1)) && b.getStartAt().isBefore(max));
						break;
						
					case SUM_PER_MONTH: //somme total des resa du mois passe
						bookingsForRules = bookingsForRules.filter(b->b.getStartAt().isAfter(max.minusMonths(1)) && b.getStartAt().isBefore(max));
						break;
				}
				
				//le total des resa est superieur au total de la regle ? => La regle est viole
				if (bookingsForRules.count() > rule.getNumberOfSession()){
					return true;
				}
				
				return false;
			}
		};
	}
}
