package org.crossfit.app.service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredForBookingException;
import org.crossfit.app.exception.rules.SubscriptionDateNotYetAvaiblableException;
import org.crossfit.app.exception.rules.SubscriptionException;
import org.crossfit.app.exception.rules.SubscriptionMembershipRulesException;
import org.crossfit.app.exception.rules.SubscriptionMembershipRulesException.MembershipRulesExceptionType;
import org.crossfit.app.exception.rules.SubscriptionNoMembershipRulesApplicableException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BookingRulesChecker {

    private static final Logger log = LoggerFactory.getLogger(BookingRulesChecker.class);
	
    private final DateTime now;
	private final Map<Subscription, List<Booking>> bookingsBySubscriptions;
	private final Set<Subscription> subscriptions;


	public BookingRulesChecker(DateTime now){
		this(now, new ArrayList<>(), new HashSet<>());
	}
	
	public BookingRulesChecker(DateTime now, List<Booking> bookings, Set<Subscription> subscriptions) {
		super();
		this.now = now;
		this.bookingsBySubscriptions = bookings.stream().collect(Collectors.groupingBy(Booking::getSubscription));
		this.subscriptions = new HashSet<>(subscriptions);
	}

	public Optional<MembershipRules> breakRulesToCancel(Booking booking, Collection<MembershipRules> rulesToVerify){

		Optional<MembershipRules> breakingRule = rulesToVerify.stream().filter(isRuleApplyFor(booking)).filter(r->{
			DateTime cancellableBefore = booking.getStartAt().minusHours(r.getNbHoursAtLeastToCancel());			
			return now.isAfter(cancellableBefore);
		}).findFirst();
		
		return breakingRule;
	}

	public Subscription findSubscription(Member owner, TimeSlotType timeSlotType, DateTime startAt, int totalBookingForTimeSlot) 
			throws ManySubscriptionsAvailableException, NoSubscriptionAvailableException {

		log.debug("Il est {}. Recherche d'une souscription valide pour {} {} pour un creneau {} le {} parmis ses souscription {}",
				now, owner.getFirstName(), owner.getLastName(), timeSlotType.getName(), startAt, subscriptions);

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

				if (subscription.getSubscriptionStartDate() != null && subscription.getSubscriptionStartDate().isAfter(startAt.toLocalDate())){
					throw new SubscriptionDateNotYetAvaiblableException(subscription, booking);
				}
							
				//On ajoute à toute les resa passée, celle en cours de préparation
				List<Booking> subscriptionBookings = new ArrayList<>();
				if (bookingsBySubscriptions.get(subscription) != null){
					subscriptionBookings.addAll(bookingsBySubscriptions.get(subscription));
				}
				booking.setSubscription(subscription);
				subscriptionBookings.add(booking);
				
				//On récupère les regles de gestion associé à cet abonnement
				Set<MembershipRules> rules = subscription.getMembership().getMembershipRules();
				boolean noneMatch = rules.stream().noneMatch(isRuleApplyFor(booking));
				if (noneMatch){
					throw new SubscriptionNoMembershipRulesApplicableException(subscription, booking);
				}
				
				Map<MembershipRulesExceptionType, Predicate<? super MembershipRules>> rulesToTest = new HashMap<>();
				rulesToTest.put(MembershipRulesExceptionType.CountPreviousBooking, isRuleBreakingCountPreviousBooking(booking, subscriptionBookings));
				rulesToTest.put(MembershipRulesExceptionType.NbHoursAtLeastToBook, isRuleBreakingNbHoursAtLeastToBook(booking, totalBookingForTimeSlot));
				rulesToTest.put(MembershipRulesExceptionType.NbMaxBooking, isRuleBreakingNbMaxBooking(subscriptionBookings));
				rulesToTest.put(MembershipRulesExceptionType.NbMaxDayBooking, isRuleBreakingNbMaxDayBooking(booking));
				
				for (Entry<MembershipRulesExceptionType, Predicate<? super MembershipRules>> entry : rulesToTest.entrySet()) {
					//On cherche les regles violées
					List<MembershipRules> breakingRules = rules.stream().filter(entry.getValue()).collect(Collectors.toList());
					
					if (!breakingRules.isEmpty()){
						throw new SubscriptionMembershipRulesException(entry.getKey(), subscription, booking, breakingRules);
					}
				}
				
				

				log.debug("La resa {} peut se faire via la souscription {}", booking, subscription);
				possibleSubscriptionsToUse.add(subscription);

			} catch (SubscriptionDateExpiredException
					| SubscriptionDateExpiredForBookingException
					| SubscriptionMembershipRulesException | SubscriptionNoMembershipRulesApplicableException | SubscriptionDateNotYetAvaiblableException e) {				
				
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
    
	
	
	private Predicate<? super MembershipRules> isRuleBreakingCountPreviousBooking(Booking bookingToTest, List<Booking> bookings) {
		final List<Booking> _bookings = bookings;

		return new Predicate<MembershipRules>() {

			@Override
			public boolean test(MembershipRules rule) {
				if(rule.getNumberOfSession() < 0) //illimité => Valid
					return false;
				
				Optional<Booking> lastBooking = _bookings.stream().filter(b->isRuleApplyFor(b).test(rule)).max(Comparator.comparing((Booking::getStartAt)));
				DateTime lastBookingDate = lastBooking.map(Booking::getStartAt).orElse(bookingToTest.getStartAt()).plusDays(1);
				//On cherche les résa a un type de creneau ou s'appplique la regle
				List<Booking> bookingsForRules = _bookings.stream().filter(b->isRuleApplyFor(b).test(rule)).collect(Collectors.toList());
				
				long bookingsCount = 0;
				DateTime deb = bookingToTest.getStartAt();
				switch (rule.getType()) {
					case SUM: //somme total des resa
						bookingsCount = bookingsForRules.size();
						break;

					case SUM_PER_WEEK: //somme total des resa de la semaine passe
						deb = bookingToTest.getStartAt().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay(); //TODO: Attention, le lundi c'est pas forcement le devut de la semaine !
						bookingsCount = countMaxBouking(deb, lastBookingDate, d->d.plusWeeks(1), bookingsForRules, rule);
						break;
						
					case SUM_PER_4_WEEKS: //somme total des resa des 4 semaines passees
						Optional<Booking> previousBookingBeforeNow = _bookings.stream().filter(b->isRuleApplyFor(b).test(rule) && b.getStartAt().isBefore(bookingToTest.getStartAt())).max(Comparator.comparing((Booking::getStartAt)));
						DateTime deb4Week = previousBookingBeforeNow.map(Booking::getStartAt).orElse(bookingToTest.getStartAt());
						deb = deb4Week.withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
						bookingsCount = countMaxBouking(deb, lastBookingDate, d->d.plusWeeks(4), bookingsForRules, rule);
						break;
						
					case SUM_PER_MONTH: //somme total des resa du mois passe
						deb = bookingToTest.getStartAt().withDayOfMonth(1).withTimeAtStartOfDay(); 
						bookingsCount = countMaxBouking(deb, lastBookingDate, d->d.plusMonths(1), bookingsForRules, rule);
						break;
				}

				//le total des resa est superieur au total de la regle ? => La regle est viole

				log.debug("Suivant la regle {} limitant a {} resa, on a compte au max {} résa entre le {} et le {}", rule.getType(), rule.getNumberOfSession(), bookingsCount, deb, lastBookingDate);

				
				if (bookingsCount > rule.getNumberOfSession()){					
					return true;
				}
				
				return false;
			}
		};
	}
	
	private static final long countMaxBouking(DateTime deb, DateTime finalEnd, Function<DateTime, DateTime> increment, Collection<Booking> bookingsForRules, MembershipRules rule){
		long maxCount = 0;
		while(deb.isBefore(finalEnd)){
			DateTime after = deb;
			DateTime before = increment.apply(deb);
			
			long count = bookingsForRules.stream().filter(b->b.getStartAt().isAfter(after) && b.getStartAt().isBefore(before)).count();
			if (count > maxCount){
				maxCount = count;
				log.debug("{} résa entre le {} et le {}. La regle {} limite {} résa.", count, after, before, rule.getType(), rule.getNumberOfSession());
			}
			deb = before;
		}
		return maxCount;
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
				long count = _bookings.stream().filter(b->isRuleApplyFor(b).test(rule))
						.filter(b->{
							return b.getStartAt().isAfter(now);
						}).count();
				
				if (count > rule.getNbMaxBooking()){
					log.debug("Il y a deja {} resa apres le {}, et le max est {}", count, now,  rule.getNbMaxBooking());

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
