package org.crossfit.app.repository;

import java.util.List;
import java.util.Optional;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotNotification;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotNotificationRepository extends JpaRepository<TimeSlotNotification,Long> {

	Optional<TimeSlotNotification> findOneByDateAndTimeSlotAndMember(LocalDate date, TimeSlot timeslot, Member member);
	List<TimeSlotNotification> findAllByDate(LocalDate date);

}
