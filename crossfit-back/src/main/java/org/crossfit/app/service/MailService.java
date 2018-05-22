package org.crossfit.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.mail.CrossfitMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

	private final Logger log = LoggerFactory.getLogger(MailService.class);

	@Inject
	private CrossfitMailSender mailSender;

	@Inject
	private MessageSource messageSource;

	@Inject
	private SpringTemplateEngine templateEngine;

	@Async
	public void sendActivationEmail(Member member, String clearPassword) {
		log.debug("Sending activation e-mail to '{}'", member.getLogin());
		Locale locale = Locale.forLanguageTag(member.getLangKey() == null ? "fr" : member.getLangKey());
		Context context = new Context(locale);
		context.setVariable("user", member);
		context.setVariable("clearPassword", clearPassword);
		log.debug("Sending activation e-mail to '{}'", clearPassword);
		context.setVariable("box", member.getBox());
		String content = templateEngine.process("activationCompte", context);
		String subject = messageSource.getMessage("email.creation.title", new Object[] { member.getBox().getName() },
				locale);
		mailSender.sendEmail(member.getBox().getEmailFrom(), member.getLogin(), subject, content, false, true);
	}

	public void sendNotification(List<TimeSlotNotification> findAllByDateAndTimeSlot) {
		if (findAllByDateAndTimeSlot == null || findAllByDateAndTimeSlot.isEmpty()){
			log.debug("Reservation supprimée, mais personne ne souhaite être notifié");
			return;
		}
		for (TimeSlotNotification timeSlotNotification : findAllByDateAndTimeSlot) {
			sendNotification(timeSlotNotification);
		}
	}

	
	public void sendNotification(TimeSlotNotification notif) {
		
		final SimpleDateFormat sdfDateDisplay = new SimpleDateFormat("dd/MM/yyyy");
		final SimpleDateFormat sdfDateBooking = new SimpleDateFormat("yyyy-MM-dd");
		final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		
		Member member = notif.getMember();
		log.debug("Sending notification e-mail to '{}'", member.getLogin());
		Locale locale = Locale.forLanguageTag(member.getLangKey() == null ? "fr" : member.getLangKey());
		Context context = new Context(locale);
		context.setVariable("user", member);
		context.setVariable("notif", notif);
		
		Date dateResa = notif.getDate().toDateTime(notif.getTimeSlot().getStartTime()).toDate();
		context.setVariable("timeSlotId", notif.getTimeSlot().getId());
		context.setVariable("timeSlotDate", sdfDateDisplay.format(dateResa));
		context.setVariable("timeSlotTime", sdfTime.format(dateResa));
		
		String link = member.getBox().getBookingwebsite()+"#/planning/mobile/day/"+sdfDateBooking.format(dateResa)+"/"+notif.getTimeSlot().getId()+"/"+sdfDateBooking.format(dateResa);
		context.setVariable("linkResa", link);
		context.setVariable("box", member.getBox());
		String content = templateEngine.process("timeSlotNotification", context);
		String subject = messageSource.getMessage("email.timeSlotNotification.title", 
				new Object[] { }, locale);
		mailSender.sendEmail(member.getBox().getEmailFrom(), member.getLogin(), subject, content, false, true);
	}
}
