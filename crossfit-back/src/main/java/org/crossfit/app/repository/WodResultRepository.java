package org.crossfit.app.repository;

import java.util.Set;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.workouts.Wod;
import org.crossfit.app.domain.workouts.WodResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WodResultRepository  extends JpaRepository<WodResult,Long> {

    @Query("select r from WodResult r "
    		+ "where r.wod.id = :wodId "
    		+ "and r.wod.box = :box "
    		+ "and r.member = :member ")
	Set<WodResult> findAll(@Param("box") CrossFitBox box, @Param("wodId") Long wodId, @Param("member") Member member);


}
