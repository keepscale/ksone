package org.crossfit.app.repository;

import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.CrossFitBox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BillsBucket {
	Page<Bill> findAllBillNumberLikeForBoxOrderByNumberDesc(String year, CrossFitBox box, Pageable pageable);
	Bill save(Bill bill);
}
