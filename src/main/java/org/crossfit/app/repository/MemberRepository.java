package org.crossfit.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Member entity.
 */
public interface MemberRepository extends JpaRepository<Member,Long> {

    @Query("select m from Member m where m.box = :box "
    		+ "and ( "
    		+ "	lower(m.firstName) like :search "
    		+ "	or lower(m.lastName) like :search "
    		+ "	or lower(m.telephonNumber) like :search "
    		+ "	or lower(m.login) like :search "
    		+ ") "
    		+ "and ( "
    		+ "		( true = :includeActif 		AND m.enabled = true 	and m.locked = false ) "
    		+ "or 	( true = :includeNotEnabled AND m.enabled = false  	and m.locked = false ) "
    		+ "or 	( true = :includeBloque 	AND m.locked  = true ) "
    		+ ") "
    		+ "and ( "
    		+ "		( true = :includeAllMembership ) "
    		+ "or exists ( select s from Subscription s where s.member = m and s.membership.id in ( :includeMembershipsIds ) ) "
    		+ ") "
    		+ "order by m.enabled DESC, m.locked ASC, m.lastName, m.firstName")
	Page<Member> findAll(@Param("box") CrossFitBox box, @Param("search") String search, 
			@Param("includeMembershipsIds") Set<Long> includeMembershipsIds, @Param("includeAllMembership") boolean includeAllMembership,
			@Param("includeActif") boolean includeActif,@Param("includeNotEnabled")boolean includeNotEnabled, @Param("includeBloque")boolean includeBloque, 
			Pageable pageable);

    @Query("select m from Member m "
    		+ "left join fetch m.authorities "
    		+ "left join fetch m.subscriptions s "
    		+ "left join fetch s.membership ms "
    		+ "left join fetch ms.membershipRules msr "
    		+ "left join fetch msr.applyForTimeSlotTypes "
    		+ "where m.login = :login and m.box = :box")
    Optional<Member> findOneByLogin(@Param("login") String login, @Param("box") CrossFitBox currentCrossFitBox);

    
    @Query("select m from Member m where m.cardUuid = :cardUuid and m.box = :box")
    Optional<Member> findOneByCardUuid(@Param("cardUuid") String cardUuid, @Param("box") CrossFitBox currentCrossFitBox);

    @Query("select m from Member m where m.box = :box and m.enabled = false")
    List<Member> findAllUserNotEnabled(@Param("box") CrossFitBox box);

    @Query("select m from Member m where m.box = :box and :role in elements(m.authorities) ")
    List<Member> findAllUserWithRole(@Param("box") CrossFitBox box, @Param("role") Authority role);

    
    @Modifying(clearAutomatically=true)
	@Transactional
	@Query("UPDATE Member m SET m.cardUuid = :cardUuid where m.id = :id")
	void updateCardUuid(@Param("id") Long id, @Param("cardUuid") String cardUuid);

    @Modifying(clearAutomatically=true)
	@Transactional
	@Query("UPDATE Member m SET m.cardUuid = NULL where m.cardUuid = :cardUuid")
	void clearUsageCardUuid(@Param("cardUuid") String cardUuid);

    

}
