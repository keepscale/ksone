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
    
    @Query("select s from Subscription s  "
    		+ "left join fetch s.membership ms "
    		+ "left join fetch ms.membershipRules msr "
    		+ "left join fetch msr.applyForTimeSlotTypes "
    		+ "where s.id = :id")
	Subscription findOneWithRules(@Param("id") Long id);

    @Query("select s from Subscription s where s.member = :member and s.subscriptionEndDate is null")
	Subscription findActiveByMember(@Param("member") Member member);

	List<Subscription> findAllByMembership(Membership membership);
   
	
	 @Query("select s from Subscription s "
	 		+ "join s.membership ms "
	 		+ "join s.member m "
	 		+ "where m.box = :box "
	    		+ "and ( "
	    		+ "	lower(m.firstName) like :search "
	    		+ "	or lower(m.lastName) like :search "
	    		+ "	or lower(m.telephonNumber) like :search "
	    		+ "	or lower(m.login) like :search "
	    		+ ") "
	    		+ "order by s.subscriptionEndDate desc, m.lastName, m.firstName")
	Page<Subscription> findAllSubscriptionOfMemberLike(@Param("box") CrossFitBox box, @Param("search") String search, Pageable pageable);

	 
	 @Query("select s from Subscription s "
		 		+ "join fetch s.membership ms "
		 		+ "join fetch s.member m "
		 		+ "where ms.box = :box")
	 List<Subscription> findAllByBoxWithMembership(@Param("box") CrossFitBox box);

	 @Query("select s from Subscription s "
	 		+ "join fetch s.membership ms "
	 		+ "join fetch s.member m "
	 		+ "where ms.box = :box AND s.subscriptionStartDate <= :at AND :at < s.subscriptionEndDate")
	List<Subscription> findAllByBoxAtDate(@Param("box") CrossFitBox box, @Param("at")  org.joda.time.LocalDate at);

}
