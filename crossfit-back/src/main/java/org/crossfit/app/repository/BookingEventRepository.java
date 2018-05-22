package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.BookingEvent;
import org.crossfit.app.domain.CrossFitBox;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the BookingEvent entity.
 */
public interface BookingEventRepository extends JpaRepository<BookingEvent,Long> {

    @Query("select e from BookingEvent e where e.box = :box AND e.bookingStartDate BETWEEN :start AND :end")
	List<BookingEvent> findAllByBookingStartBetween(@Param("box") CrossFitBox box, @Param("start") DateTime start, @Param("end") DateTime end);

	
}
