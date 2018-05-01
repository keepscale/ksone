package org.crossfit.app.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.crossfit.app.Application;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.repository.TimeSlotTypeRepository;
import org.crossfit.app.service.util.BookingRulesChecker;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class BookingRulesCheckerTest {

    private static final Logger log = LoggerFactory.getLogger(BookingRulesCheckerTest.class);
	
	
	@Inject
	private CrossFitBoxSerivce crossFitBoxSerivce;
	@Inject
	private MemberRepository memberRepository;
	@Inject
	private MembershipRepository membershipRepository;
	@Inject
	private TimeSlotTypeRepository timeSlotTypeRepository;
	

	private Membership ABO_TRIPLE;
	private Membership ABO_5_PAR_MOIS;
	private TimeSlotType WOD;
	private TimeSlotType OPENBOX;
	private Member aMember;

	@Before
	public void initTest() {
		aMember = memberRepository.findOne(1L);
		ABO_TRIPLE = membershipRepository.findOne(6L, crossFitBoxSerivce.findCurrentCrossFitBox());
		ABO_5_PAR_MOIS = membershipRepository.findOne(8L, crossFitBoxSerivce.findCurrentCrossFitBox());
		WOD = timeSlotTypeRepository.findOne(3L);
		OPENBOX = timeSlotTypeRepository.findOne(5L);
	}

	@Test
	public void testRegleParSemaine() throws ManySubscriptionsAvailableException, NoSubscriptionAvailableException {

		Subscription sTriple = new Subscription();
		sTriple.setMember(aMember);
		sTriple.setMembership(ABO_TRIPLE);
		Set<Subscription> subscriptions = new HashSet<Subscription>();
		subscriptions.add(sTriple);
		
		List<Booking> bookings = new ArrayList<Booking>();

		bookings.add(createBooking("2016-10-10T09:00:00", sTriple, OPENBOX)); 	//lundi
		bookings.add(createBooking("2016-10-12T09:00:00", sTriple, OPENBOX)); 	//mercredi
		bookings.add(createBooking("2016-10-14T09:00:00", sTriple, OPENBOX)); 	//vendredi
		
		bookings.add(createBooking("2016-10-12T14:00:00", sTriple, WOD));		//mercredi
		bookings.add(createBooking("2016-10-14T14:00:00", sTriple, WOD));		//vendredi		
		
		bookings.add(createBooking("2016-10-17T14:00:00", sTriple, WOD)); 		//lundi d'apres
		bookings.add(createBooking("2016-10-18T14:00:00", sTriple, WOD)); 		//mardi d'apres
		bookings.add(createBooking("2016-10-19T14:00:00", sTriple, WOD)); 		//mercredi d'apres
		
		
		DateTime now = parseDateTime("2016-10-11T14:00:00"); 					//On est mardi
		BookingRulesChecker checker = new BookingRulesChecker(now , bookings, subscriptions);

		checker.findSubscription(aMember, WOD, parseDateTime("2016-10-12T09:00:00"), 5); //On doit pouvoir reserver le mercredi
		checker.findSubscription(aMember, WOD, parseDateTime("2016-10-13T09:00:00"), 5); //On doit pouvoir reserver le jeudi
		checker.findSubscription(aMember, WOD, parseDateTime("2016-10-14T09:00:00"), 5); //On doit pouvoir reserver le vendredi
		checker.findSubscription(aMember, WOD, parseDateTime("2016-10-15T09:00:00"), 5); //On doit pouvoir reserver le samedi
		checker.findSubscription(aMember, WOD, parseDateTime("2016-10-16T09:00:00"), 5); //On doit pouvoir reserver le dimanche

		try {
			checker.findSubscription(aMember, WOD, parseDateTime("2016-10-17T09:00:00"), 5); //pas le lundi d'apres ! car deja 3 resa la semaine d'apres
			Assert.fail("pas le lundi d'apres ! car deja 3 resa la semaine d'apres");
		} catch (Exception e) {
			log.debug("Exception nomale: " + e.getMessage());
		}
		try {
			checker.findSubscription(aMember, WOD, parseDateTime("2016-10-20T09:00:00"), 5); //pas le jeudi d'apres ! car deja 3 resa la semaine d'apres
			Assert.fail("pas le jeudi d'apres ! car deja 3 resa la semaine d'apres");
		} catch (Exception e) {
			log.debug("Exception nomale: " + e.getMessage());
		}

		checker.findSubscription(aMember, WOD, parseDateTime("2016-10-24T09:00:00"), 5); //On doit pouvoir reserver le lundi dans 2 semaines

	}
	

	@Test
	public void testRegleParMois() throws ManySubscriptionsAvailableException, NoSubscriptionAvailableException {

		Subscription sTripleParMois = new Subscription();
		sTripleParMois.setMember(aMember);
		sTripleParMois.setMembership(ABO_5_PAR_MOIS);
		Set<Subscription> subscriptions = new HashSet<Subscription>();
		subscriptions.add(sTripleParMois);
		
		List<Booking> bookings = new ArrayList<Booking>();

		
		bookings.add(createBooking("2016-10-12T14:00:00", sTripleParMois, WOD));		//mercredi
		bookings.add(createBooking("2016-10-14T14:00:00", sTripleParMois, WOD));		//vendredi
		bookings.add(createBooking("2016-10-17T14:00:00", sTripleParMois, WOD)); 		//lundi d'apres
		bookings.add(createBooking("2016-10-18T14:00:00", sTripleParMois, WOD)); 		//mardi d'apres
		bookings.add(createBooking("2016-10-19T14:00:00", sTripleParMois, WOD)); 		//mercredi d'apres
		bookings.add(createBooking("2016-10-25T14:00:00", sTripleParMois, WOD)); 		//mardi d'apres

		bookings.add(createBooking("2016-11-02T14:00:00", sTripleParMois, WOD)); 		//mercredi mois après
		bookings.add(createBooking("2016-11-04T14:00:00", sTripleParMois, WOD)); 		//vendredi mois après
		
		DateTime now = parseDateTime("2016-10-19T14:00:00"); 					//On est mercredi
		BookingRulesChecker checker = new BookingRulesChecker(now , bookings, subscriptions);

		//On ne doit plus pouvoir réserver car on a fait les 5 résa sur le mois
		try {
			checker.findSubscription(aMember, WOD, parseDateTime("2016-10-24T09:00:00"), 5);
			Assert.fail("Pas le 24, car déjà 5 résa");
		} catch (Exception e) {
			log.debug("Exception nomale: ", e);
		}
		
		//Par contre, on doit pouvoir réserver dès le 1er du mois suivant (mardi 1 nov 2016)
		checker.findSubscription(aMember, WOD, parseDateTime("2016-11-01T14:00:00"), 5); 

	}

	private Booking createBooking(String dateStr, Subscription souscriptionDouble, TimeSlotType timeSlotType) {
		Booking b = new Booking();
		DateTime startAt = parseDateTime(dateStr);
		b.setStartAt(startAt);
		b.setEndAt(startAt.plusHours(1));
		b.setSubscription(souscriptionDouble);
		b.setTimeSlotType(timeSlotType);
		return b;
	}

	private DateTime parseDateTime(String dateStr) {
		DateTime startAt = ISODateTimeFormat.dateTimeParser().parseDateTime(dateStr);
		return startAt;
	}

	
}
