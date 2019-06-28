package org.crossfit.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.domain.TimeSlotType;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeSlotNotificationRepository extends JpaRepository<TimeSlotNotification,Long> {

	Optional<TimeSlotNotification> findOneByDateAndTimeSlotAndMember(LocalDate date, TimeSlot timeslot, Member member);
	
    @Query("select notif from TimeSlotNotification notif left join fetch notif.timeSlot ts WHERE notif.date = :date AND ts.timeSlotType =:timeSlotType AND ts.startTime = :timeSlotStartTime AND ts.endTime  = :timeSlotEndTime")
	Set<TimeSlotNotification> findAll(
			@Param("date") LocalDate date, 
			@Param("timeSlotStartTime") LocalTime timeSlotStartTime, 
			@Param("timeSlotEndTime") LocalTime timeSlotEndTime, 
			@Param("timeSlotType") TimeSlotType timeSlotType);

    @Transactional
    @Modifying
    @Query("DELETE from TimeSlotNotification notif WHERE notif.date = :date AND notif.member = :member")
	void deleteAll(
			@Param("date") LocalDate date, 
			@Param("member") Member member);

    @Query("select notif from TimeSlotNotification notif "
    		+ "left join fetch notif.member m "
    		+ "left join fetch notif.timeSlot ts "
    		+ "left join fetch ts.timeSlotType ")
	List<TimeSlotNotification> findAll();

}
