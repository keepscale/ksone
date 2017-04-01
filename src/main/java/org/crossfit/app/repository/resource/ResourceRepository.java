package org.crossfit.app.repository.resource;

import java.util.List;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.resources.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Resource entity.
 */
public interface ResourceRepository extends JpaRepository<Resource,Long> {

	List<Resource> findAllByBox(CrossFitBox box);


}
