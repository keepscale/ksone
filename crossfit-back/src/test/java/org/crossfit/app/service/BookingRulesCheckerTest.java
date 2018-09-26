package org.crossfit.app.service;

import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.exception.rules.SubscriptionAvailableWithWarningException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredException;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.repository.TimeSlotTypeRepository;
import org.crossfit.app.service.util.BookingRulesChecker;
import org.crossfit.app.web.rest.errors.ExceptionTranslator;
import org.crossfit.app.web.rest.errors.SubscriptionErrorDTO;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
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
	@Inject
	private ExceptionTranslator exceptionTranslator;
	

	private Membership ABO_TRIPLE;
	private Membership ABO_5_PAR_MOIS;
	private TimeSlotType WOD;
	private TimeSlotType OPENBOX;
	private Member aMember;

	@Before
	public void initTest() {
		aMember = memberRepository.findById(1L).get();
		ABO_TRIPLE = membershipRepository.findOne(6L, crossFitBoxSerivce.findCurrentCrossFitBox());
		ABO_5_PAR_MOIS = membershipRepository.findOne(8L, crossFitBoxSerivce.findCurrentCrossFitBox());
		WOD = timeSlotTypeRepository.findById(3L).get();
		OPENBOX = timeSlotTypeRepository.findById(5L).get();
	}
	
	
	@Test
	public void testAbdoExpireAvecUnValide() throws ManySubscriptionsAvailableException, NoSubscriptionAvailableException, SubscriptionAvailableWithWarningException {

    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	
		Set<Subscription> subscriptions = new HashSet<Subscription>();
		Subscription s6 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-11-01", "2016-12-01");
		
		Subscription s1 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-02-01", "2016-03-01");
		Subscription s2 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-03-01", "2016-03-01");
		Subscription s3 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-04-01", "2016-05-01");
		Subscription s4 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-05-01", "2016-05-01");
		Subscription s5 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-10-01", "2016-11-01");

		List<Booking> bookings = new ArrayList<Booking>();

		DateTime now = parseDateTime("2016-10-12T14:00:00");
		BookingRulesChecker checker = new BookingRulesChecker(now , bookings, subscriptions, -1);

		Subscription findSubscription = checker.findSubscription(aMember, WOD, parseDateTime("2016-10-14T09:00:00"), 0);
		
		assertThat(findSubscription, Matchers.equalTo(s5));
	}
	

	@Test
	public void testQueDesAbdoExpire() throws ManySubscriptionsAvailableException, SubscriptionAvailableWithWarningException {

    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	
		Set<Subscription> subscriptions = new HashSet<Subscription>();
		Subscription s6 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-11-01", "2016-12-01");
		
		Subscription s1 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-02-01", "2016-03-01");
		Subscription s2 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-03-01", "2016-03-01");
		Subscription s3 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-04-01", "2016-05-01");
		Subscription s4 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-05-01", "2016-05-01");
		Subscription s5 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-10-01", "2016-11-01");

		List<Booking> bookings = new ArrayList<Booking>();

		DateTime now = parseDateTime("2016-12-12T14:00:00");
		BookingRulesChecker checker = new BookingRulesChecker(now , bookings, subscriptions, -1);
		
		try {
			checker.findSubscription(aMember, WOD, parseDateTime("2016-12-14T09:00:00"), 0);
			Assert.fail("Pas de souscription dispo !");
		} catch (NoSubscriptionAvailableException e) {
			assertThat(e.getExceptions(), Matchers.hasSize(6)); //On a bien 6 erreurs d'abonnements
			
			
			SubscriptionErrorDTO exToError = exceptionTranslator.processNoSubscriptionAvailableError(e);

			assertThat(exToError.getErrors(), Matchers.hasSize(1)); //Mais après traitement, on en affiche qu'un seul !
			assertThat(exToError.getErrors().iterator().next().getMessage(), //Et le dernier
					Matchers.equalTo("Votre abonnement " + s6.getMembership().getName() + " a expiré depuis le "+ sdf.format(s6.getSubscriptionEndDate().toDate())));
		}
	}

	@Test
	public void testQueDesAbdoExpireAvecUnValideMaisErreurValiationRegle() throws ManySubscriptionsAvailableException, SubscriptionAvailableWithWarningException {

    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	
		Set<Subscription> subscriptions = new HashSet<Subscription>();
		
		Subscription s1 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-02-01", "2016-03-01");
		Subscription s2 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-03-01", "2016-03-01");
		Subscription s3 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-04-01", "2016-05-01");
		Subscription s4 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-05-01", "2016-05-01");
		Subscription s6 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-09-01", "2016-10-01");
		Subscription s5 = createSubscription(subscriptions, aMember, ABO_TRIPLE, "2016-10-01", "2016-11-01");		

		List<Booking> bookings = new ArrayList<Booking>();

		DateTime now = parseDateTime("2016-10-12T08:00:00");
		BookingRulesChecker checker = new BookingRulesChecker(now , bookings, subscriptions, -1);
		
		try {
			checker.findSubscription(aMember, WOD, parseDateTime("2016-10-12T09:00:00"), 0);
			Assert.fail("Pas de souscription dispo !");
		} catch (NoSubscriptionAvailableException e) {
			assertThat(e.getExceptions(), Matchers.hasSize(6)); //On a bien 5 erreurs d'abonnements + l'erreur de regle de réservation tardive
			assertThat(e.getExceptions().stream().filter(eee->eee instanceof SubscriptionDateExpiredException).collect(Collectors.toList()), Matchers.hasSize(5)); //5 erreur d'abo expirer
			
			SubscriptionErrorDTO exToError = exceptionTranslator.processNoSubscriptionAvailableError(e);

			assertThat(exToError.getErrors(), Matchers.hasSize(1)); //Mais après traitement, on en affiche qu'un seul !
			assertThat(exToError.getErrors().iterator().next().getMessage(), //Celui de la regle depasse
					Matchers.equalTo("Votre abonnement " + s6.getMembership().getName() + " ne vous permet pas de réserver pour ce créneau: "));
		}
	}
	
	@Test
	public void testRegleParSemaine() throws ManySubscriptionsAvailableException, NoSubscriptionAvailableException, SubscriptionAvailableWithWarningException {

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
		BookingRulesChecker checker = new BookingRulesChecker(now , bookings, subscriptions, -1);

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
	public void testRegleParMois() throws ManySubscriptionsAvailableException, NoSubscriptionAvailableException, SubscriptionAvailableWithWarningException {

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
		BookingRulesChecker checker = new BookingRulesChecker(now , bookings, subscriptions, -1);

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


	private Subscription createSubscription(Set<Subscription> subscriptions, Member member, Membership membership, String start, String end) {
		Subscription s = new Subscription();
		s.setId(subscriptions.size()+1L);
		s.setMembership(membership);
		s.setMember(member);
		s.setSubscriptionStartDate(parseDate(start));
		s.setSubscriptionEndDate(parseDate(end));
		subscriptions.add(s);
		return s;
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


	private LocalDate parseDate(String text) {
		LocalDate d = ISODateTimeFormat.localDateParser().parseLocalDate(text);
		return d;
	}
	private DateTime parseDateTime(String text) {
		DateTime dt = ISODateTimeFormat.dateTimeParser().parseDateTime(text);
		return dt;
	}

	
}
