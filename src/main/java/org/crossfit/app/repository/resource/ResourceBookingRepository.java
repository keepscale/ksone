package org.crossfit.app.repository.resource;

import java.util.List;

import org.crossfit.app.domain.resources.Resource;
import org.crossfit.app.domain.resources.ResourceBooking;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the ResourceBooking entity.
 */
public interface ResourceBookingRepository extends JpaRepository<ResourceBooking,Long> {

	List<ResourceBooking> findAllByResource(Resource resource);


}
