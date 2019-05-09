package org.crossfit.app.repository;

import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Mandate entity.
 */
public interface MandateRepository extends JpaRepository<Mandate,Long> {

    List<Mandate> findAllByMember(Member member);

    @Query("select m from Mandate m "
            + "left join fetch m.member me "
            + "where m.id = :id")
    Mandate findOneWithMember(Long id);
}
