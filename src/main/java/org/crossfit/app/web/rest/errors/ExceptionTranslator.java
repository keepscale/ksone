package org.crossfit.app.web.rest.errors;

import java.text.SimpleDateFormat;
import java.util.List;

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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
        	
        	for (SubscriptionException e : ex.getExceptions()) {
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
						}	

					}
					
				}
			}
    	}
    	
    	
        return error;
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
