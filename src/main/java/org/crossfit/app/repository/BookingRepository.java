package org.crossfit.app.repository;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Booking entity.
 */
public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Query("select b from Booking b left join fetch b.subscription s left join fetch s.member where b.box =:box AND b.startAt between :start and :end")
	Set<Booking> findAllBetween(@Param("box") CrossFitBox box, @Param("start") DateTime start, @Param("end") DateTime end);
    
    @Query("select b from Booking b where b.subscription.member = :member AND b.startAt = :start and b.endAt = :end order by b.startAt desc")
	List<Booking> findAllByMemberAndDate(@Param("member") Member member, @Param("start") DateTime start, @Param("end") DateTime end);

    @Query("select b from Booking b where b.subscription.member = :member order by b.startAt desc")
	List<Booking> findAllByMember(@Param("member") Member owner);
    
    @Query("select b from Booking b where b.subscription.member = :member order by b.startAt desc")
	Page<Booking> findAllByMember(@Param("member") Member member, Pageable pageable);
    
    @Modifying
	@Transactional
	@Query("delete from Booking b where b.subscription.member = :member")
	void deleteAllByMember(@Param("member") Member member);

}
