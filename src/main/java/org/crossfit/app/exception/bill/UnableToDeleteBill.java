package org.crossfit.app.exception.bill;

import org.crossfit.app.domain.Bill;

public class UnableToDeleteBill extends Exception {

	private final Bill bill;

	public UnableToDeleteBill(Bill billToDelete) {
		super("Impossible de supprimer la facture");
		this.bill = billToDelete;
	}

	public Bill getBill() {
		return bill;
	}

	
}
