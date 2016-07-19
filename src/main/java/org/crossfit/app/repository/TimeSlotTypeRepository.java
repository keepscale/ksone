package org.crossfit.app.repository;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlotType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the TimeSlot entity.
 */
public interface TimeSlotTypeRepository extends JpaRepository<TimeSlotType,Long> {

	Page<TimeSlotType> findAllByBox(CrossFitBox box, Pageable page);

}
