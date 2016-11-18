package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.CardEvent;
import org.crossfit.app.domain.CrossFitBox;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the CardEvent entity.
 */
public interface CardEventRepository extends JpaRepository<CardEvent,Long> {


	@Query("from CardEvent e WHERE e.box =:box AND e.bookingStartDate BETWEEN :start AND :end")
	List<CardEvent> findAllBetweenBookingStartDate(@Param("box") CrossFitBox box, @Param("start") DateTime start, @Param("end") DateTime end);
	
}
