package org.crossfit.app.repository;

import java.util.Set;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.workouts.Wod;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WodRepository  extends JpaRepository<Wod,Long> {

    @Query("select w from Wod w "
    		+ "left join fetch w.taggedMovements "
    		+ "left join fetch w.taggedEquipments "
    		+ "left join fetch w.publications "
    		+ "where w.box = :box "
    		//+ "and (w.shareProperties.visibility = 'PUBLIC' or w.shareProperties.owner = :owner) "
    		+ "and ( "
    		+ "	lower(w.name) like :search "
    		+ "	or lower(w.category) like :search "
    		+ "	or lower(w.score) like :search "
    		+ "	or lower(w.description) like :search "
    		+ ") "
    		+ "and ( true = :dontFilterPublication OR ( exists ( select pub from w.publications pub where  (:start <= pub.endAt )  AND ( pub.startAt <= :end ) ) ) )"
    		+ "")
	Set<Wod> findAll(@Param("box") CrossFitBox box, @Param("search") String search, 
			@Param("dontFilterPublication") boolean dontFilterPublication, @Param("start") LocalDate start, @Param("end") LocalDate end);


    @Query("select w from Wod w "
    		+ "left join fetch w.taggedMovements "
    		+ "left join fetch w.taggedEquipments "
    		+ "left join fetch w.publications "
    		+ "where w.box = :box and w.id = :id "
    		+ "and (w.shareProperties.visibility = 'PUBLIC' or w.shareProperties.owner = :owner) ")
	Wod findOne(@Param("box") CrossFitBox box, @Param("owner") Member owner,@Param("id")  Long id );


}
