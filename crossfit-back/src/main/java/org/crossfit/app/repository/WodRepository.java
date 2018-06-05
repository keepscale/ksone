package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.workouts.WOD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WodRepository  extends JpaRepository<WOD,Long> {

    @Query("select w from WOD w where w.box = :box "
    		+ "and ( "
    		+ "	lower(w.name) like :search "
    		+ "	or lower(w.category) like :search "
    		+ "	or lower(w.score) like :search "
    		+ "	or lower(w.description) like :search "
    		+ ") ")
	List<WOD> findAll(@Param("box") CrossFitBox box, @Param("search") String search);


    @Query("select w from WOD w where w.box = :box and w.id = :id")
	WOD findOne(@Param("box") CrossFitBox currentCrossFitBox,@Param("id")  Long id );

}
