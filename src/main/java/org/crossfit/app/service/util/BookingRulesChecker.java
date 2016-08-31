package org.crossfit.app.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.crossfit.app.exception.rules.MembershipRulesException.MembershipRulesExceptionType;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredForBookingException;
import org.crossfit.app.exception.rules.SubscriptionException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BookingRulesChecker {

    private final Logger log = LoggerFactory.getLogger(BookingRulesChecker.class);
	
    private final DateTime now;
	private final Map<Subscription, List<Booking>> bookingsBySubscriptions;
	private final Set<Subscription> subscriptions;


	public BookingRulesChecker(DateTime now, List<Booking> bookings, Set<Subscription> subscriptions) {
		super();
		this.now = now;
		this.bookingsBySubscriptions = bookings.stream().collect(Collectors.groupingBy(Booking::getSubscription));
		this.subscriptions = new HashSet<>(subscriptions);
	}


	public Subscription findSubscription(Member owner, TimeSlotType timeSlotType, DateTime startAt, int totalBookingForTimeSlot) throws ManySubscriptionsAvailableException, NoSubscriptionAvailableException {

		log.debug("Recherche d'une souscription valide pour {} {} pour un creneau {} le {} parmis ses souscription {}",
				owner.getFirstName(), owner.getLastName(), timeSlotType.getName(), startAt, subscriptions);

		Booking booking = new Booking();
        booking.setTimeSlotType(timeSlotType);
        booking.setStartAt(startAt);
                
        List<Subscription> possibleSubscriptionsToUse = new ArrayList<>();
        List<SubscriptionException> subscriptionsInvalid = new ArrayList<>();
         
		for (Subscription subscription : subscriptions) {
			
			try {
				if(subscription.getSubscriptionEndDate() != null && subscription.getSubscriptionEndDate().isBefore(now.toLocalDate()))
					throw new SubscriptionDateExpiredException(subscription);

				if(subscription.getSubscriptionEndDate() != null && subscription.getSubscriptionEndDate().isBefore(startAt.toLocalDate()))
					throw new SubscriptionDateExpiredForBookingException(subscription, booking);

							
				//On ajoute à toute les resa passée, celle en cours de préparation
				List<Booking> subscriptionBookings = new ArrayList<>();
				if (bookingsBySubscriptions.get(subscription) != null){
					subscriptionBookings.addAll(bookingsBySubscriptions.get(subscription));
				}
				booking.setSubscription(subscription);
				subscriptionBookings.add(booking);
				
				//On récupère les regles de gestion associé à cet abonnement
				Set<MembershipRules> rules = subscription.getMembership().getMembershipRules();
				
				Map<MembershipRulesExceptionType, Predicate<? super MembershipRules>> rulesToTest = new HashMap<>();
				rulesToTest.put(MembershipRulesExceptionType.CountPreviousBooking, isRuleBreakingCountPreviousBooking(subscriptionBookings));
				rulesToTest.put(MembershipRulesExceptionType.NbHoursAtLeastToBook, isRuleBreakingNbHoursAtLeastToBook(booking, totalBookingForTimeSlot));
				rulesToTest.put(MembershipRulesExceptionType.NbMaxBooking, isRuleBreakingNbMaxBooking(subscriptionBookings));
				rulesToTest.put(MembershipRulesExceptionType.NbMaxDayBooking, isRuleBreakingNbMaxDayBooking(booking));
				
				for (Entry<MembershipRulesExceptionType, Predicate<? super MembershipRules>> entry : rulesToTest.entrySet()) {
					//On cherche les regles violées
					List<MembershipRules> breakingRules = rules.stream().filter(entry.getValue()).collect(Collectors.toList());
					
					if (!breakingRules.isEmpty()){
						throw new MembershipRulesException(entry.getKey(), subscription, booking, breakingRules);
					}
				}
				
				

				log.debug("La resa {} peut se faire via la souscription {}", booking, subscription);
				possibleSubscriptionsToUse.add(subscription);

			} catch (SubscriptionDateExpiredException
					| SubscriptionDateExpiredForBookingException
					| MembershipRulesException e) {				
				
				log.debug("Souscrpition {} invalide: {}", subscription, e.getMessage());
				
				subscriptionsInvalid.add(e);
			}
		}
		
		if (possibleSubscriptionsToUse.isEmpty()){
			log.debug("Aucune souscription valide pour la resa {]", booking);
			throw new NoSubscriptionAvailableException(subscriptionsInvalid);
		}
		else if (possibleSubscriptionsToUse.size() == 1){
			log.debug("Une seul souscription possible pour cette résa");
			return possibleSubscriptionsToUse.get(0);
		}
		else{
			log.debug("Plusieurs souscription possible pour cette résa");
			throw new ManySubscriptionsAvailableException(possibleSubscriptionsToUse);
		}
		
	}
    
	
	
	private Predicate<? super MembershipRules> isRuleBreakingCountPreviousBooking(List<Booking> bookings) {
		final List<Booking> _bookings = bookings;

		return new Predicate<MembershipRules>() {

			@Override
			public boolean test(MembershipRules rule) {
				if(rule.getNumberOfSession() < 0) //illimité => Valid
					return false;
				
				//On cherche les résa a un type de creneau ou s'appplique la regle
				Stream<Booking> bookingsForRules = _bookings.stream().filter(b->isRuleApplyFor(b).test(rule));
				
				switch (rule.getType()) {
					case SUM: //somme total des resa
						break;

					case SUM_PER_WEEK: //somme total des resa de la semaine passe
						bookingsForRules = bookingsForRules.filter(b->b.getStartAt().isAfter(now.minusWeeks(1)) && b.getStartAt().isBefore(now));
						break;
						
					case SUM_PER_MONTH: //somme total des resa du mois passe
						bookingsForRules = bookingsForRules.filter(b->b.getStartAt().isAfter(now.minusMonths(1)) && b.getStartAt().isBefore(now));
						break;
				}
				
				//le total des resa est superieur au total de la regle ? => La regle est viole
				if (bookingsForRules.count() > rule.getNumberOfSession()){

					log.debug("Le total des resa ({}) est superieur au total de la regle {} ({})", bookingsForRules.count(), rule.getType(), rule.getNumberOfSession());
					
					return true;
				}
				
				return false;
			}
		};
	}
	
	private Predicate<? super MembershipRules> isRuleApplyFor(Booking booking) {

		return new Predicate<MembershipRules>() {

			public boolean test(MembershipRules rule) {
				//La regle courante s'applique t elle a la nouvelle résa ?
				return rule.getApplyForTimeSlotTypes().contains(booking.getTimeSlotType());
			}
		};
	}
	
	private Predicate<? super MembershipRules> isRuleBreakingNbHoursAtLeastToBook(Booking bookingToTest, int totalBookingForTimeSlot) {

		return new Predicate<MembershipRules>() {

			public boolean test(MembershipRules rule) {
				//La regle courante s'applique t elle a la nouvelle résa ?
				if (isRuleApplyFor(bookingToTest).test(rule)){

					//Pas encore de résa pour ce créneau
					if (totalBookingForTimeSlot == 0){
						//La date de la résa est après le nombre d'heure min pour réserver => ok
						boolean isBookingBeforeNbHoursAtLeastToBook = bookingToTest.getStartAt().isBefore(now.plusHours(rule.getNbHoursAtLeastToBook()));
						if (isBookingBeforeNbHoursAtLeastToBook){
							log.debug("Il n'y a pas encore de résa pour le {} et il est {}, alors qu'il faut reserver {} à l'avance", bookingToTest.getStartAt(), now, rule.getNbHoursAtLeastToBook());
							return true;
						}
					}
				}
				
				return false;
			}
		};
	}
	
	private Predicate<? super MembershipRules> isRuleBreakingNbMaxBooking(List<Booking> bookings) {
		final List<Booking> _bookings = bookings;
		
		return new Predicate<MembershipRules>() {

			public boolean test(MembershipRules rule) {
				//On cherche les résa a un type de creneau ou s'appplique la regle et après maintenant
				Stream<Booking> bookingsForRules = _bookings.stream().filter(b->isRuleApplyFor(b).test(rule))
						.filter(b->{
							return b.getStartAt().isAfter(now);
						});
				
				if (bookingsForRules.count() > rule.getNbMaxBooking()){
					log.debug("Il y a deja {} resa apres le {}, et le max est {}", bookingsForRules.count(), now,  rule.getNbMaxBooking());

					return true;
				}
								
				return false;
			}
		};
	}
	
	private Predicate<? super MembershipRules> isRuleBreakingNbMaxDayBooking(Booking bookingToTest) {
		
		return new Predicate<MembershipRules>() {

			public boolean test(MembershipRules rule) {
				//La regle courante s'applique t elle a la nouvelle résa ?
				if (isRuleApplyFor(bookingToTest).test(rule)){

					//La date de la résa est après le nombre d'heure min pour réserver => ok
					boolean isBookingAfterMaxDayToBook = bookingToTest.getStartAt().isAfter(now.plusDays(rule.getNbMaxDayBooking()));
					if (isBookingAfterMaxDayToBook){
						log.debug("Nous sommes le {} et la resa pour le {} est apres les {} jours max de réservation autorise à l'avance", now, bookingToTest.getStartAt(), rule.getNbMaxDayBooking());
						return true;
					}
				}
				
				return false;
			}
		};
	}
}
