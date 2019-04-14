package org.crossfit.app.repository;

import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Mandate entity.
 */
public interface MandateRepository extends JpaRepository<Mandate,Long> {

    List<Mandate> findAllByMember(Member member);
}
