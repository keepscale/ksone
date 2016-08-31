package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.TimeSlotExclusion;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the TimeSlotExclusion entity.
 */
public interface TimeSlotExclusionRepository extends JpaRepository<TimeSlotExclusion,Long> {

	@Query("select tse from TimeSlotExclusion tse where tse.date between :start and :end")
	List<TimeSlotExclusion> findAllBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
