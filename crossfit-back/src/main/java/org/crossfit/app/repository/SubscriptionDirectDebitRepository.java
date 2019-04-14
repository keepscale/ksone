package org.crossfit.app.repository;

import org.crossfit.app.domain.*;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the SubscriptionDirectDebit entity.
 */
public interface SubscriptionDirectDebitRepository extends JpaRepository<SubscriptionDirectDebit,Long> {

}
