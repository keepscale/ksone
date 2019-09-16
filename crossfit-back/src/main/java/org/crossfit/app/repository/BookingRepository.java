package org.crossfit.app.repository;

import java.util.List;
import java.util.Set;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlotType;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Booking entity.
 */
public interface BookingRepository extends JpaRepository<Booking,Long> {
	
    public static final String START_BETWEEN = ""
    		+ "select b from Booking b "
    		+ "join fetch b.timeSlotType t "
    		+ "join fetch b.subscription s "
    		+ "join fetch s.member "
    		+ "join fetch s.membership ms "
    		+ "WHERE b.box =:box AND b.startAt BETWEEN :start AND :end";

	@Query(START_BETWEEN)
	Set<Booking> findAllStartBetween(@Param("box") CrossFitBox box, @Param("start") DateTime start, @Param("end") DateTime end);
	
	@Query(START_BETWEEN + " AND s.member = :member ")
	Set<Booking> findAllStartBetween(@Param("box") CrossFitBox box, @Param("member") Member member, 
			@Param("start") DateTime start, 
			@Param("end") DateTime end);
    
    @Query("select b from Booking b left join fetch b.subscription s left join fetch s.member left join fetch s.membership ms where b.box =:box AND b.startAt = :start and b.endAt = :end")
 	Set<Booking> findAllAt(@Param("box") CrossFitBox box, @Param("start") DateTime start, @Param("end") DateTime end);
     
    @Query("select b from Booking b where b.subscription.member = :member AND b.startAt = :start and b.endAt = :end order by b.startAt desc")
	List<Booking> findAllByMemberAndDate(@Param("member") Member member, @Param("start") DateTime start, @Param("end") DateTime end);

    @Query("select b from Booking b where b.subscription.member = :member order by b.startAt desc")
	List<Booking> findAllByMember(@Param("member") Member owner);
    
    @Query("select b from Booking b join b.subscription s join s.membership ms where s.member = :member and b.startAt >= :after order by b.startAt asc")
	Page<Booking> findAllByMemberAfter(@Param("member") Member member, @Param("after") DateTime after, Pageable pageable);
    
    @Modifying
	@Transactional
	@Query("delete from Booking b where b.subscription.member = :member")
	void deleteAllByMember(@Param("member") Member member);

    Long countBySubscription(Subscription subscription);

	@Query("select count(1) from Booking b where b.subscription = :sub and b.startAt <= :before")
    Long countBySubscriptionBefore(@Param("sub") Subscription subscription, @Param("before") DateTime beforeIncl);

	@Query("select count(1) from Booking b where b.subscription = :sub and :after <= b.startAt and b.startAt <= :before")
    Long countBySubscriptionBetween(@Param("sub") Subscription subscription, @Param("after") DateTime startIncl, @Param("before") DateTime endIncl);

	@Query("select count(1) from Booking b where b.timeSlotType = :timeSlotType "
			+ "AND EXTRACT(isodow from b.startAt) = :day "
			+ "AND HOUR(b.startAt) = :startHour "
			+ "AND MINUTE(b.startAt) = :startMinute "
			+ "AND HOUR(b.endAt) = :endHour "
			+ "AND MINUTE(b.endAt) = :endMinute ")
    Integer countByTimeSlot( @Param("timeSlotType") TimeSlotType timeSlotType, @Param("day") Integer dayOfWeek, 
    		@Param("startHour") Integer startHour, @Param("startMinute") Integer startMinute, 
    		@Param("endHour") Integer endHour, @Param("endMinute") Integer endMinute);

	@Query("select count(1) from Booking b where b.timeSlotType = :timeSlotType AND b.startAt = :start AND b.endAt = :end")
    Integer countByTimeSlot(@Param("timeSlotType") TimeSlotType timeSlotType,
    		@Param("start") DateTime start, @Param("end") DateTime end);
}
