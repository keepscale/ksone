package org.crossfit.app.repository;

import java.util.Set;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.workouts.Wod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WodRepository  extends JpaRepository<Wod,Long> {

    @Query("select w from Wod w "
    		+ "left join fetch w.taggedMovements "
    		+ "left join fetch w.taggedEquipments "
    		+ "left join fetch w.dates "
    		+ "where w.box = :box "
    		+ "and ( "
    		+ "	lower(w.name) like :search "
    		+ "	or lower(w.category) like :search "
    		+ "	or lower(w.score) like :search "
    		+ "	or lower(w.description) like :search "
    		+ ") ")
	Set<Wod> findAll(@Param("box") CrossFitBox box, @Param("search") String search);


    @Query("select w from Wod w "
    		+ "left join fetch w.taggedMovements "
    		+ "left join fetch w.taggedEquipments "
    		+ "left join fetch w.dates "
    		+ "where w.box = :box and w.id = :id")
	Wod findOne(@Param("box") CrossFitBox currentCrossFitBox,@Param("id")  Long id );

}
