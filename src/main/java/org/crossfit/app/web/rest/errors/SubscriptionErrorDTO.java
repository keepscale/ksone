package org.crossfit.app.web.rest.errors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.crossfit.app.domain.TimeSlotType;

public class SubscriptionErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String message;
    private final List<SubscriptionMessageErreur> detail;

    public SubscriptionErrorDTO(String message) {
        this.message = message;
        this.detail = new ArrayList<SubscriptionErrorDTO.SubscriptionMessageErreur>();
    }

    public String getMessage() {
        return message;
    }
    
    

    
    public List<SubscriptionMessageErreur> getDetail() {
		return detail;
	}

	public SubscriptionMessageErreur addDetail(String messageKey, String... params) {
		SubscriptionMessageErreur e = new SubscriptionMessageErreur(messageKey, params);
		detail.add(e);
		return e;
	}
    
    
	public static class SubscriptionMessageErreur{
	    private final String[] params;
    	private final String messageKey;
    	private final List<RulesErreur> reason;
		public SubscriptionMessageErreur(String messageKey, String... params) {
			super();
			this.messageKey = messageKey;
			this.params = params;
			this.reason = new ArrayList<RulesErreur>();
		}
    	
		public void addReason(Set<TimeSlotType> types, String messageKey, String... params){
			this.reason.add(new RulesErreur(new ArrayList<TimeSlotType>(types), messageKey, params));
		}

		public String[] getParams() {
			return params;
		}

		public String getMessageKey() {
			return messageKey;
		}

		public List<RulesErreur> getReason() {
			return reason;
		}
    	
    }
	

	public static class RulesErreur{
		private final String messageKey;
		private final String[] params;
    	private final List<TimeSlotType> types;
    	
		public RulesErreur(List<TimeSlotType> types, String messageKey, String... params) {
			super();
			this.types = types;
			this.messageKey = messageKey;
			this.params = params;
		}

		public String getMessageKey() {
			return messageKey;
		}

		public String[] getParams() {
			return params;
		}

		public List<TimeSlotType> getTypes() {
			return types;
		}
    	
    }


}
