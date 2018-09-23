package org.crossfit.app.repository;

import java.util.Optional;
import java.util.Set;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.domain.TimeSlotType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeSlotNotificationRepository extends JpaRepository<TimeSlotNotification,Long> {

	Optional<TimeSlotNotification> findOneByDateAndTimeSlotAndMember(LocalDate date, TimeSlot timeslot, Member member);
	
    @Query("select notif from TimeSlotNotification notif left join fetch notif.timeSlot ts where notif.date = :date AND ts.timeSlotType =:timeSlotType AND ts.startTime = :timeSlotStartTime AND ts.endTime  = :timeSlotEndTime")
	Set<TimeSlotNotification> findAll(
			@Param("date") LocalDate date, 
			@Param("timeSlotStartTime") LocalTime timeSlotStartTime, 
			@Param("timeSlotEndTime") LocalTime timeSlotEndTime, 
			@Param("timeSlotType") TimeSlotType timeSlotType);

}
