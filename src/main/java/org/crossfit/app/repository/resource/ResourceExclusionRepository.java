package org.crossfit.app.repository.resource;

import org.crossfit.app.domain.resources.ResourceExclusion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Resource entity.
 */
public interface ResourceExclusionRepository extends JpaRepository<ResourceExclusion,Long> {


}
