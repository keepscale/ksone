package org.crossfit.app.exception.bill;

import org.crossfit.app.domain.Bill;

public class UnableToDeleteBillException extends Exception {

	private final Bill bill;

	public UnableToDeleteBillException(Bill billToDelete) {
		super("Impossible de supprimer la facture");
		this.bill = billToDelete;
	}

	public Bill getBill() {
		return bill;
	}

	
}
