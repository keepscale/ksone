package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.workouts.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Movement entity.
 */
public interface MovementRepository extends JpaRepository<Movement,Long> {

    @Query("select m from Movement m where "
    		+ "( "
    		+ "	lower(m.shortname) like :search "
    		+ "	or lower(m.fullname) like :search "
    		+ ") "
    		+ "order by m.fullname ASC")
	List<Movement> findAll(@Param("search") String search);

    

}
