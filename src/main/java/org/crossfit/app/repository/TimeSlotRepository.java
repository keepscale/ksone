package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.joda.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the TimeSlot entity.
 */
public interface TimeSlotRepository extends JpaRepository<TimeSlot,Long> {

	@Query("select t from TimeSlot t where t.box =:box AND t.startTime = :start and t.endTime = :end")
	TimeSlot findByIdAndTime(@Param("box") CrossFitBox box, @Param("start") LocalTime start, @Param("end") LocalTime end);

	List<TimeSlot> findAllByTimeSlotType(TimeSlotType timeSlotType);
}
