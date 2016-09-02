package org.crossfit.app.repository;

import java.util.List;
import java.util.Set;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
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

    @Query("select s from Subscription s  "
    		+ "left join fetch s.membership ms "
    		+ "left join fetch ms.membershipRules msr "
    		+ "left join fetch msr.applyForTimeSlotTypes "
    		+ "where s.member = :member")
	Set<Subscription> findAllByMember(@Param("member") Member member);

    @Query("select s from Subscription s where s.member = :member and s.subscriptionEndDate is null")
	Subscription findActiveByMember(@Param("member") Member member);

	List<Subscription> findAllByMembership(Membership membership);
   
	
	 @Query("select s from Subscription s "
	 		+ "left join fetch s.membership "
	 		+ "where s.member.box = :box "
	    		+ "and ( "
	    		+ "	lower(s.member.firstName) like :search "
	    		+ "	or lower(s.member.lastName) like :search "
	    		+ "	or lower(s.member.telephonNumber) like :search "
	    		+ "	or lower(s.member.login) like :search "
	    		+ ") "
	    		+ "order by s.member.lastName, s.member.firstName")
	Page<Subscription> findAllSubscriptionOfMemberLike(@Param("box") CrossFitBox box, @Param("search") String search, Pageable pageable);

}
