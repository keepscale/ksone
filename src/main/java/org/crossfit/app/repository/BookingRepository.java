package org.crossfit.app.repository;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for the Booking entity.
 */
public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Query("select b from Booking b where b.box =:box AND b.startAt between :start and :end")
	List<Booking> findAll(@Param("box") CrossFitBox box, @Param("start") DateTime start, @Param("end") DateTime end);
    
    @Query("select b from Booking b where b.box =:box AND b.owner = :member AND b.startAt = :start and b.endAt = :end order by b.startAt desc")
	List<Booking> findAllByMemberAndDate(@Param("box") CrossFitBox box, @Param("member") Member member, @Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("select b from Booking b where b.box =:box AND b.owner = :member order by b.startAt desc")
	Page<Booking> findAllByMember(@Param("box") CrossFitBox box, @Param("member") Member member, Pageable pageable);
    
    @Query("select b from Booking b where b.box =:box AND b.owner = :member AND b.startAt between :start and :end")
	List<Booking> findAllByMemberForPlanning(@Param("box") CrossFitBox box, @Param("member") Member member, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Modifying
	@Transactional
	@Query("delete from Booking b where b.box =:box AND b.owner = :member")
	void deleteAllByMember(@Param("box") CrossFitBox currentCrossFitBox, @Param("member") Member member);
}
