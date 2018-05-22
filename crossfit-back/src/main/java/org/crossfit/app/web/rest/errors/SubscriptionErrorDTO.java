package org.crossfit.app.web.rest.errors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlotType;

public class SubscriptionErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String message;
    private List<SubscriptionMessageErreur> errors;
    private List<Subscription> possibleSubscriptions;

    public SubscriptionErrorDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    public List<SubscriptionMessageErreur> getErrors() {
		return errors;
	}
    

	public List<Subscription> getPossibleSubscriptions() {
		return possibleSubscriptions;
	}

	public void setPossibleSubscriptions(List<Subscription> possibleSubscriptions) {
		this.possibleSubscriptions = possibleSubscriptions;
	}

	public SubscriptionMessageErreur addDetail(String message) {
		if (this.errors == null)
			this.errors = new ArrayList<SubscriptionErrorDTO.SubscriptionMessageErreur>();
		SubscriptionMessageErreur e = new SubscriptionMessageErreur(message);
		errors.add(e);
		return e;
	}
    
    
	public static class SubscriptionMessageErreur{
    	private final String message;
    	private final List<RulesErreur> reasons;
		public SubscriptionMessageErreur(String message) {
			super();
			this.message = message;
			this.reasons = new ArrayList<RulesErreur>();
		}
    	
		public void addReason(Set<TimeSlotType> types, String message){
			this.reasons.add(new RulesErreur(types.stream().map(TimeSlotType::getName).collect(Collectors.joining(", ")), message));
		}


		public String getMessage() {
			return message;
		}

		public List<RulesErreur> getReasons() {
			return reasons;
		}
    	
    }
	

	public static class RulesErreur{
		private final String message;
    	
		public RulesErreur(String types, String message) {
			super();
			this.message = String.format(message, types);
		}

		public String getMessage() {
			return message;
		}

    }


}
