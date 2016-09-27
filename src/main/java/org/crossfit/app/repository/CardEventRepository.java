package org.crossfit.app.repository;

import org.crossfit.app.domain.CardEvent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the CardEvent entity.
 */
public interface CardEventRepository extends JpaRepository<CardEvent,Long> {
	
}
