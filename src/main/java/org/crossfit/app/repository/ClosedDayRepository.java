package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.CrossFitBox;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the ClosedDay entity.
 */
public interface ClosedDayRepository extends JpaRepository<ClosedDay,Long> {
	
    @Query("select day from ClosedDay day where day.box =:box AND day.startAt between :startAt and :endAt")
	List<ClosedDay> findAllByBoxAndBetween(@Param("box") CrossFitBox box, @Param("startAt") DateTime startAt, @Param("endAt") DateTime endAt);

	static final String BY_ID = " cd.id = :id ";
	static final String BY_BOX = " cd.box = :box ";
	
	@Query("select cd from ClosedDay cd where" + BY_BOX + " order by cd.startAt asc")
	List<ClosedDay> findAll(@Param("box") CrossFitBox box);

	@Query("select cd from ClosedDay cd where" + BY_ID + " and " + BY_BOX)
	ClosedDay findOne(@Param("id") Long id, @Param("box") CrossFitBox currentCrossFitBox);

	@Modifying
	@Transactional
	@Query("delete from ClosedDay cd where" + BY_ID + " and " + BY_BOX)
	void delete(@Param("id") Long id, @Param("box") CrossFitBox currentCrossFitBox);


}
