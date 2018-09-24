package org.crossfit.app.web.rest.errors;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.enumeration.MembershipRulesType;
import org.crossfit.app.exception.EmailAlreadyUseException;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredForBookingException;
import org.crossfit.app.exception.rules.SubscriptionDateNotYetAvaiblableException;
import org.crossfit.app.exception.rules.SubscriptionException;
import org.crossfit.app.exception.rules.SubscriptionMembershipRulesException;
import org.crossfit.app.exception.rules.SubscriptionNoMembershipRulesApplicableException;
import org.crossfit.app.web.rest.errors.SubscriptionErrorDTO.SubscriptionMessageErreur;
import org.joda.time.Chronology;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
public class ExceptionTranslator {

    @ExceptionHandler(EmailAlreadyUseException.class)
    public ResponseEntity<String> processEmailAlreadyUseError(MethodArgumentNotValidException ex) {
		return ResponseEntity.badRequest().header("Failure", "Cet email est deja attribué a quelqu'un d'autre.").body(null);
    }
    
    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO processConcurencyError(ConcurrencyFailureException ex) {
        return new ErrorDTO(ErrorConstants.ERR_CONCURRENCY_FAILURE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return processFieldErrors(fieldErrors);
    }

    @ExceptionHandler(CustomParameterizedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorDTO processParameterizedValidationError(CustomParameterizedException ex) {
        return ex.getErrorDTO();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO processAccessDeniedExcpetion(AccessDeniedException e) {
        return new ErrorDTO(ErrorConstants.ERR_ACCESS_DENIED, e.getMessage());
    }

    private ErrorDTO processFieldErrors(List<FieldError> fieldErrors) {
        ErrorDTO dto = new ErrorDTO(ErrorConstants.ERR_VALIDATION);

        for (FieldError fieldError : fieldErrors) {
            dto.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getCode());
        }

        return dto;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDTO processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return new ErrorDTO(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, exception.getMessage());
    }
    
    

    @ExceptionHandler(NoSubscriptionAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public SubscriptionErrorDTO processNoSubscriptionAvailableError(NoSubscriptionAvailableException ex) {
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	SubscriptionErrorDTO error;
    	
    	if (ex.getExceptions().isEmpty()){
        	error = new SubscriptionErrorDTO("Vous n'avez pas d'abonnement");
    	}
    	else{
        	error = new SubscriptionErrorDTO("Vous ne pouvez pas réserver ce créneau");
        	
        	List<SubscriptionException> exceptions = prefilterExceptionToDisplay(ex);        	
        	
			for (SubscriptionException e : exceptions) {
				String membershipName = e.getSubscription().getMembership().getName();
				String dateFin = sdf.format(e.getSubscription().getSubscriptionEndDate().toDate());
				String dateDeb = sdf.format(e.getSubscription().getSubscriptionStartDate().toDate());
				if (e instanceof SubscriptionDateExpiredException){
					error.addDetail("Votre abonnement " + membershipName + " a expiré depuis le "+ dateFin);					
				}
				else if (e instanceof SubscriptionDateExpiredForBookingException){
					SubscriptionDateExpiredForBookingException ee = (SubscriptionDateExpiredForBookingException) e;
					String dateBooking = sdf.format(ee.getBooking().getStartAt().toDate());
					error.addDetail("Votre abonnement " + membershipName + " expire le "+ dateFin + ". Vous ne pouvez pas réserver pour le "+ dateBooking);
				}
				else if (e instanceof SubscriptionDateNotYetAvaiblableException){
					SubscriptionDateNotYetAvaiblableException ee = (SubscriptionDateNotYetAvaiblableException) e;
					String dateBooking = sdf.format(ee.getBooking().getStartAt().toDate());
					error.addDetail("Votre abonnement " + membershipName + " démarre le "+ dateDeb + ". Vous ne pouvez pas encore réserver pour le "+ dateBooking);
				}
				else if (e instanceof SubscriptionNoMembershipRulesApplicableException){
					SubscriptionNoMembershipRulesApplicableException ee = (SubscriptionNoMembershipRulesApplicableException) e;
					String timeSlotTypeName = ee.getBooking().getTimeSlotType().getName();
					error.addDetail("Votre abonnement " + membershipName + " ne vous permet pas de réserver des créneaux " + timeSlotTypeName);
				}
				else if (e instanceof SubscriptionMembershipRulesException){
					
					SubscriptionMembershipRulesException rule = (SubscriptionMembershipRulesException) e;

					String timeSlotType = rule.getBooking().getTimeSlotType().getName();

					PeriodFormatter formatter = new PeriodFormatterBuilder()
					    .appendYears().appendSuffix(" an", " ans")
					    .toFormatter();
    
					SubscriptionMessageErreur detail = error.addDetail("Votre abonnement " + membershipName + " ne vous permet pas de réserver pour ce créneau: ");
					
					for (MembershipRules r : rule.getBreakingRules()) {
						switch (rule.getType()) {
							case CountPreviousBooking:
								MembershipRulesType type = r.getType();
								String message = "Vous avez atteint votre quota de " + r.getNumberOfSession() + " sessions ";
								switch (type) {
									case SUM:
										message += " au total.";
										break;
									case SUM_PER_WEEK:
										message += " par semaine.";
										break;
									case SUM_PER_4_WEEKS:
										message += " sur les 4 semaines écoulées.";
										break;
									case SUM_PER_MONTH:
										message += " par mois.";
										break;
								}
								detail.addReason(r.getApplyForTimeSlotTypes(), message);
								break;
		
							case NbHoursAtLeastToBook:
								detail.addReason(r.getApplyForTimeSlotTypes(), "Les séances %s doivent être réservées au moins " + r.getNbHoursAtLeastToBook() + " heures à l'avance.");
								break;
		
		
							case NbMaxBooking:
								detail.addReason(r.getApplyForTimeSlotTypes(), "Vous ne pouvez réserver que " + r.getNbMaxBooking() + " séances %s à l'avance.");
								break;
									
		
							case NbMaxDayBooking:	
								detail.addReason(r.getApplyForTimeSlotTypes(), "Les séances %s ne peuvent être réservées que " + r.getNbMaxDayBooking() + " jours à l'avance.");
								break;
							case MedicalCertificate:
									
								String message2 = "Un certificat médical";
								if (r.getMedicalCertificateValidForLessThanNbYears() > 0) {
									message2 += " de moins de " + formatter.print(Period.years(r.getMedicalCertificateValidForLessThanNbYears()));
								}
								message2 += " est obligatoire pour réserver.";
								if (!rule.getOwner().hasGivenMedicalCertificate() || rule.getOwner().getMedicalCertificateDate() == null) {
									message2 +=  " Veuillez nous contacter pour mettre à jour vos informations personnelles.";
								}
								else {
									message2 += " Le dernier certificat fourni date du " + sdf.format(rule.getOwner().getMedicalCertificateDate().toDate());
								}
								detail.addReason(r.getApplyForTimeSlotTypes(), message2);
								break;
						}	

					}
					
				}
			}
    	}
    	
    	
        return error;
    }

	private List<SubscriptionException> prefilterExceptionToDisplay(NoSubscriptionAvailableException ex) {
		List<SubscriptionException> exceptions = ex.getExceptions()
				.stream()
				.filter(e->!(e instanceof SubscriptionDateExpiredException))
				.collect(Collectors.toList()); //On enlève toutes les exceptions des abos expirés
		
		if (exceptions.isEmpty()) { //Si la liste des exceptions est vide (c'est qu'il n'y a que des exceptions d'abo expirés)
			//Alors on va afficher que l'exception du dernier abo
			Optional<SubscriptionDateExpiredException> lastException = ex.getExceptions()
				.stream()
				.filter(e->e instanceof SubscriptionDateExpiredException)
				.map(e->(SubscriptionDateExpiredException)e)
				.collect(Collectors.maxBy(Comparator.comparing(e->e.getSubscription().getSubscriptionEndDate())));
			if (lastException.isPresent()) {
				exceptions.add(lastException.get());
			}
			else { //Impossible a atteindre normalement, mais dans le doute, on affiche toutes les exceptions
				exceptions = ex.getExceptions();
			}
		}
		return exceptions;
	}
    

    @ExceptionHandler(ManySubscriptionsAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public SubscriptionErrorDTO processManySubscriptionsAvailableExceptionError(ManySubscriptionsAvailableException ex) {
    	SubscriptionErrorDTO error = new SubscriptionErrorDTO("Avec quel abonnement souhaitez-vous réserver ce créneau ?");
    	error.setPossibleSubscriptions(ex.getSubscriptions());
    	return error;
    }
}
