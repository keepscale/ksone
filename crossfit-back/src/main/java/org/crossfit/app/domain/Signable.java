package org.crossfit.app.domain;

import org.joda.time.DateTime;

public interface Signable {

	public String getSignatureDataEncoded() ;
	public void setSignatureDataEncoded(String signatureDataEncoded);
	
	public DateTime getSignatureDate();
	public void setSignatureDate(DateTime signatureDate);
}
