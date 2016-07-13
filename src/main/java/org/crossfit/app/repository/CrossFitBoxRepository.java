package org.crossfit.app.repository;

import org.crossfit.app.domain.CrossFitBox;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the CrossFitBox entity.
 */
public interface CrossFitBoxRepository extends JpaRepository<CrossFitBox,Long> {
}
