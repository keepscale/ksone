package org.crossfit.app.service;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.mail.Email;
import org.crossfit.app.mail.EmailAttachment;
import org.crossfit.app.mail.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import aQute.lib.base64.Base64;

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

	@Autowired
	private MailSender mailSender;

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

	public void sendNotification(Collection<TimeSlotNotification> findAllByDateAndTimeSlot) {
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

	public void sendSubscription(Subscription subscription, byte[] pdf) {
		Member member = subscription.getMember();
		Locale locale = Locale.forLanguageTag(member.getLangKey() == null ? "fr" : member.getLangKey());
		Context context = new Context(locale);
		context.setVariable("user", member);
		context.setVariable("sub", subscription);

		String content = templateEngine.process("subscriptionNotification", context);
		String subject = messageSource.getMessage("email.subscriptionNotification.title", new Object[] { }, locale);
		Email email = new Email(member.getBox().getEmailFrom(), member.getLogin(), subject, content, true, true);
		email.addAttachment(new EmailAttachment(Base64.encodeBase64(pdf), UUID.randomUUID().toString(), "attachment", "contrat.pdf", MediaType.APPLICATION_PDF_VALUE));
		mailSender.sendEmail(email );
	}
}
