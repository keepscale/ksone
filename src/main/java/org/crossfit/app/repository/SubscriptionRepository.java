package org.crossfit.app.repository;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Subscription entity.
 */
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    @Query("select s from Subscription s where s.member = :member")
	Page<Subscription> findAllByMember(@Param("member") Member member, Pageable pageable);

    @Query("select s from Subscription s where s.member = :member and s.subscriptionEndDate is null")
	Subscription findActiveByMember(@Param("member") Member member);
   

}
