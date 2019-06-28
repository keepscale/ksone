package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the TimeSlot entity.
 */
public interface TimeSlotRepository extends JpaRepository<TimeSlot,Long> {

	@Query("select ts from TimeSlot ts join fetch ts.timeSlotType")
	List<TimeSlot> findAll();
	List<TimeSlot> findAllByTimeSlotType(TimeSlotType timeSlotType);
}
