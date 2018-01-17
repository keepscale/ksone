package org.crossfit.app.exception.bill;

import org.crossfit.app.domain.Bill;

public class UnableToUpdateBillException extends Exception {

	private final Bill bill;

	public UnableToUpdateBillException(Bill billToDelete) {
		super("Impossible de mettre à jour la facture");
		this.bill = billToDelete;
	}

	public Bill getBill() {
		return bill;
	}


}
