package org.crossfit.app.repository;

import org.crossfit.app.domain.CrossFitBox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BillsBucket<S> {
	Page<S> findAllBillNumberLikeForBoxOrderByNumberDesc(String year, CrossFitBox box, Pageable pageable);
	S save(S bill);
}
