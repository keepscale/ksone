package org.crossfit.app.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.joda.time.LocalDate;
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
    		+ "	or lower(m.comments) like :search "
    		+ "	or lower(m.number) like :search "
    		+ ") "
    		+ "and ( "
    		+ "		( true = :includeActif 		AND m.enabled = true 	and m.locked = false ) "
    		+ "or 	( true = :includeNotEnabled AND m.enabled = false  	and m.locked = false ) "
    		+ "or 	( true = :includeBloque 	AND m.locked  = true ) "
    		+ ") "
    		+ "and ( "
    		+ "		(true = :includeAllRole ) "
    		+ " or exists (select a from Authority a where a in elements( m.authorities ) and a.name in ( :includeRoles ) ) "
    		+ " or m.authorities is empty "
    		+ ") "
    		+ "and ( "
    		+ "		( true = :includeAllMembership ) "
    		+ "or exists ( select s from Subscription s where s.member = m and s.membership.id in ( :includeMembershipsIds ) ) "
    		+ "or m.subscriptions is empty "
    		+ ") "
    		+ "order by m.enabled DESC, m.locked ASC, m.lastName, m.firstName")
	List<Member> findAll(@Param("box") CrossFitBox box, @Param("search") String search, 
			@Param("includeMembershipsIds") Set<Long> includeMembershipsIds, 
			@Param("includeAllMembership") boolean includeAllMembership,
			@Param("includeRoles") Set<String> roles, 
			@Param("includeAllRole") boolean includeAllRole,
			@Param("includeActif") boolean includeActif,@Param("includeNotEnabled")boolean includeNotEnabled, @Param("includeBloque")boolean includeBloque);

    @Query("select m from Member m "
    		+ "left join fetch m.authorities "
    		+ "left join fetch m.subscriptions s "
    		+ "left join fetch s.membership ms "
    		+ "left join fetch ms.membershipRules msr "
    		+ "left join fetch msr.applyForTimeSlotTypes "
    		+ "where m.login = :login and m.box = :box")
    Optional<Member> findOneByLogin(@Param("login") String login, @Param("box") CrossFitBox currentCrossFitBox);
    
    
    @Query("select m from Member m "
    		+ "left join fetch m.authorities "
    		+ "where m.id = :id")
    Member findOne(@Param("id") Long id);


	@Query("select m from Member m "
			+ "left join fetch m.authorities "
			+ "left join fetch m.mandates "
			+ "left join fetch m.subscriptions s "
			+ "left join fetch s.directDebit sdeb "
			+ "left join fetch sdeb.mandate mandate "
			+ "left join fetch s.contractModel cm "
			+ "where m.id = :id")
	Member findOneForUpdate(@Param("id") Long id);

	@Query("select m from Member m where m.cardUuid = :cardUuid and m.box = :box")
    Optional<Member> findOneByCardUuid(@Param("cardUuid") String cardUuid, @Param("box") CrossFitBox currentCrossFitBox);

    @Query("select m from Member m where m.box = :box and m.enabled = false")
    List<Member> findAllUserNotEnabled(@Param("box") CrossFitBox box);

    @Modifying(clearAutomatically=true)
	@Transactional
	@Query("UPDATE Member m SET m.cardUuid = :cardUuid where m.id = :id")
	void updateCardUuid(@Param("id") Long id, @Param("cardUuid") String cardUuid);

    @Modifying(clearAutomatically=true)
	@Transactional
	@Query("UPDATE Member m SET m.cardUuid = NULL where m.cardUuid = :cardUuid")
	void clearUsageCardUuid(@Param("cardUuid") String cardUuid);

    @Query("select m from Member m where m.uuid = :uuid")
	Optional<Member> findOneByUuid(@Param("uuid") String uuid);

    @Query("select m from Member m where m.box = :box "
    		+ "AND m.enabled = true AND m.locked = false "
    		+ "and not exists ( "
    		+ "select s from Subscription s where s.member = m AND ( ( true = :includeAllMembership ) OR s.membership.id in ( :includeMembershipsIds ) ) "
    		+ "and s.subscriptionStartDate <= :at AND :at < s.subscriptionEndDate ) ")
	List<Member> findAllMemberWithNoSubscriptionAtDate(
			@Param("box") CrossFitBox box, 
			@Param("at") LocalDate at, 
			@Param("includeMembershipsIds") Set<Long> includeMembershipsIds, 
			@Param("includeAllMembership") boolean includeAllMembership);

    @Query("select m from Member m where m.box = :box "
    		+ "AND m.enabled = true AND m.locked = false "
    		+ "and exists ( "
    		+ "select s from Subscription s where s.member = m AND ( ( true = :includeAllMembership ) OR s.membership.id in ( :includeMembershipsIds ) ) "
    		+ "and s.subscriptionStartDate <= :at AND :at < s.subscriptionEndDate )")
	List<Member> findAllMemberWithSubscriptionAtDate(
			@Param("box") CrossFitBox box, 
			@Param("at") LocalDate at, 
			@Param("includeMembershipsIds") Set<Long> includeMembershipsIds, 
			@Param("includeAllMembership") boolean includeAllMembership);

    @Query("select m from Member m where m.box = :box "
    		+ "AND m.enabled = true AND m.locked = false "
    		+ "and (m.cardUuid is null or m.cardUuid = '') ")
	List<Member> findAllMemberWithNoCard(@Param("box") CrossFitBox box);

    @Query("select m from Member m where m.box = :box "
    		+ "AND m.enabled = true AND m.locked = false "
    		+ "and ( "
    		+ "m.address is null or m.address = ''"
    		+ "or m.zipCode is null or m.zipCode = ''"
    		+ "or m.city is null or m.city = ''"
    		+ ") ")
	List<Member> findAllMemberWithNoAddress(@Param("box") CrossFitBox box);

    @Query("select m from Member m where m.box = :box "
    		+ "AND m.locked = false "
    		+ "and exists ( "
    		+ "select s from Subscription s join s.directDebit dd left join dd.mandate mandate where s.member = m AND ( dd.mandate is null OR mandate.status != 'ACTIVE' ) ) ")
	List<Member> findAllMemberWithSubscriptionDirectDebitAndNoMandateValidate(@Param("box") CrossFitBox box);

    

}
