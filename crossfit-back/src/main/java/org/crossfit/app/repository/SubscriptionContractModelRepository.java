package org.crossfit.app.repository;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.SubscriptionContractModel;
import org.crossfit.app.domain.SubscriptionDirectDebit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the SubscriptionContractModel entity.
 */
public interface SubscriptionContractModelRepository extends JpaRepository<SubscriptionContractModel,Long> {

    List<SubscriptionContractModel> findAllByBox(CrossFitBox box);
}
