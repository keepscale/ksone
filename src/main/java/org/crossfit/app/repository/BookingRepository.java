package org.crossfit.app.repository;

import java.util.List;
import java.util.Set;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
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
    		+ "left join fetch b.subscription s "
    		+ "left join fetch s.member "
    		+ "left join fetch s.membership ms "
    		+ "WHERE b.box =:box AND b.startAt BETWEEN :start AND :end";

	@Query(START_BETWEEN)
	Set<Booking> findAllStartBetween(@Param("box") CrossFitBox box, @Param("start") DateTime start, @Param("end") DateTime end);
	
	@Query(START_BETWEEN + " AND s.member = :member ")
	Set<Booking> findAllStartBetween(@Param("box") CrossFitBox box, @Param("member") Member member, 
			@Param("start") LocalDateTime start, 
			@Param("end") LocalDateTime end);
    
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

}
