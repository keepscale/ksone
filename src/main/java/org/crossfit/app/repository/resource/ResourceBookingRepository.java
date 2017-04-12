package org.crossfit.app.repository.resource;

import java.util.List;
import java.util.Set;

import org.crossfit.app.domain.resources.Resource;
import org.crossfit.app.domain.resources.ResourceBooking;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the ResourceBooking entity.
 */
public interface ResourceBookingRepository extends JpaRepository<ResourceBooking,Long> {

	List<ResourceBooking> findAllByResource(Resource resource);

    @Query("select b from ResourceBooking b left join fetch b.resource where b.resource =:resource AND ( (:start <= b.startAt and b.startAt < :end) or (b.startAt <= :start and :start < b.endAt) )")
 	Set<ResourceBooking> findAllBetweenExcluded(@Param("resource") Resource resource, @Param("start") DateTime start, @Param("end") DateTime end);


}
