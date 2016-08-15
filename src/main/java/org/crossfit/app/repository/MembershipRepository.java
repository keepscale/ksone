package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Membership entity.
 */
public interface MembershipRepository extends JpaRepository<Membership, Long> {

	static final String BY_ID = " ms.id = :id ";
	static final String BY_BOX = " ms.box = :box ";
	
	@Query("select ms from Membership ms where" + BY_BOX + " order by ms.name")
	List<Membership> findAll(@Param("box") CrossFitBox box);

	@Query("select ms from Membership ms left join fetch ms.membershipRules msr left join fetch msr.applyForTimeSlotTypes where" + BY_ID + " and " + BY_BOX)
	Membership findOne(@Param("id") Long id, @Param("box") CrossFitBox currentCrossFitBox);

	@Modifying
	@Transactional
	@Query("delete from Membership ms where" + BY_ID + " and " + BY_BOX)
	void delete(@Param("id") Long id, @Param("box") CrossFitBox currentCrossFitBox);

}
