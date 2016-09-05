package org.crossfit.app.web.rest.errors;

import java.text.SimpleDateFormat;
import java.util.List;

import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.exception.EmailAlreadyUseException;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredException;
import org.crossfit.app.exception.rules.SubscriptionDateExpiredForBookingException;
import org.crossfit.app.exception.rules.SubscriptionException;
import org.crossfit.app.exception.rules.SubscriptionMembershipRulesException;
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
        	error = new SubscriptionErrorDTO("Votre abonnement ne vous permet pas de réserver ce créneau");
        	
        	for (SubscriptionException e : ex.getExceptions()) {
				if (e instanceof SubscriptionDateExpiredException){
					error.addDetail("message.subscription.expried", e.getSubscription().getMembership().getName(), sdf.format(e.getSubscription().getSubscriptionEndDate().toDate()));
					
				}
				else if (e instanceof SubscriptionDateExpiredForBookingException){
					error.addDetail("message.subscription.will.expried", e.getSubscription().getMembership().getName(), sdf.format(e.getSubscription().getSubscriptionEndDate().toDate()));
				}
				else if (e instanceof SubscriptionMembershipRulesException){
					SubscriptionMembershipRulesException rule = (SubscriptionMembershipRulesException) e;

					SubscriptionMessageErreur detail = error.addDetail("message.subscription.break.rule", e.getSubscription().getMembership().getName());
					
					
					for (MembershipRules r : rule.getBreakingRules()) {
						String[] params = null;
						switch (rule.getType()) {
							case CountPreviousBooking:
								params = new String[]{
										String.valueOf(r.getNumberOfSession()), 
										r.getType().toString()};
								break;
		
							case NbHoursAtLeastToBook:

								params = new String[]{
										String.valueOf(r.getNbHoursAtLeastToBook())
								};
								
								break;
		
		
							case NbMaxBooking:
								params = new String[]{
										String.valueOf(r.getNbMaxBooking())
								};
								
								break;
									
		
							case NbMaxDayBooking:
								params = new String[]{
										String.valueOf(r.getNbMaxDayBooking())
								};								
								break;
						}	
						detail.addReason(r.getApplyForTimeSlotTypes(), "message.subscription.break.rule." + rule.getType().name().toLowerCase(), params);

					}
					
				}
			}
    	}
    	
    	
        return error;
    }
    

    @ExceptionHandler(ManySubscriptionsAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorDTO processManySubscriptionsAvailableExceptionError(ManySubscriptionsAvailableException ex) {
        return new ParameterizedErrorDTO("vous avez plusieurs abonnements valable", null);
    }
}
