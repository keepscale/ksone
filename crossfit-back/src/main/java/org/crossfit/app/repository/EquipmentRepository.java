package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.workouts.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Equipment entity.
 */
public interface EquipmentRepository extends JpaRepository<Equipment,Long> {

    @Query("select e from Equipment e where "
    		+ "( "
    		+ "	lower(e.shortname) like :search "
    		+ "	or lower(e.fullname) like :search "
    		+ ") "
    		+ "order by e.fullname ASC")
	List<Equipment> findAll(@Param("search") String search);

    

}
