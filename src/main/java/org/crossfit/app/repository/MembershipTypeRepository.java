package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the MembershipType entity.
 */
public interface MembershipTypeRepository extends JpaRepository<MembershipType, Long> {

	static final String BY_ID = " mt.id = :id ";
	static final String BY_BOX = " mt.box = :box ";
	
	@Query("select mt from MembershipType mt where" + BY_BOX)
	List<MembershipType> findAll(@Param("box") CrossFitBox box);

	@Query("select mt from MembershipType mt where" + BY_ID + " and " + BY_BOX)
	MembershipType findOne(@Param("id") Long id, @Param("box") CrossFitBox currentCrossFitBox);

	@Modifying
	@Transactional
	@Query("delete from MembershipType mt where" + BY_ID + " and " + BY_BOX)
	void delete(@Param("id") Long id, @Param("box") CrossFitBox currentCrossFitBox);

}
