package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.PersistentToken;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends JpaRepository<PersistentToken, String> {

    List<PersistentToken> findByMember(Member Member);

    List<PersistentToken> findByTokenDateBefore(LocalDate localDate);

}
