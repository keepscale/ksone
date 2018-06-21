package org.crossfit.app.repository;

import java.util.Set;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.workouts.Wod;
import org.crossfit.app.domain.workouts.WodResult;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WodResultRepository  extends JpaRepository<WodResult,Long> {

    @Query("select r from WodResult r "
    		+ "where r.wod.id = :wodId "
    		+ "and r.wod.box = :box "
    		+ "and r.member = :member ")
	Set<WodResult> findAll(@Param("box") CrossFitBox box, @Param("wodId") Long wodId, @Param("member") Member member);


    @Query("select r from WodResult r "
    		+ "left join fetch r.member "
    		+ "where r.wod = :wod "
    		+ "and r.date = :date ")
	Set<WodResult> findAll(@Param("wod") Wod wod, @Param("date") LocalDate date);
    
    @Query("select r from WodResult r "
    		+ "join fetch r.wod "
    		+ "join fetch r.member "
    		+ "where r.wod.box = :box "
    		+ "and r.member = :member "
    		+ "and :start <= r.date  AND r.date <= :end ")
	Set<WodResult> findAll(@Param("box") CrossFitBox box,@Param("member") Member member, @Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("select r from WodResult r "
    		+ "where r.id = :id "
    		+ "and r.wod = :wod "
    		+ "and r.member = :member ")
	WodResult findOne( @Param("id") Long id, @Param("wod")  Wod wod,  @Param("member") Member member);


}
