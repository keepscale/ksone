package org.crossfit.app.repository.resource;

import java.util.Set;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.resources.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Resource entity.
 */
public interface ResourceRepository extends JpaRepository<Resource,Long> {

	@Query("select r from Resource r left join fetch r.rules where r.box = :box")
	Set<Resource> findAllByBox(@Param("box") CrossFitBox box);


	@Query("select r from Resource r left join fetch r.rules where r.id = :id and r.box = :box")
	Resource findOne(@Param("id") Long id, @Param("box") CrossFitBox currentCrossFitBox);

}
