package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.CrossFitBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Bill entity.
 */
public interface BillRepository extends JpaRepository<Bill,Long> {

    @Query(
    		"select b "
    		+ "from Bill b "
    		+ "where b.box =:box AND b.number like :year "
    		+ "order by b.number desc ")
	List<Bill> findLastBillInYearForBox(@Param("year") String year, @Param("box") CrossFitBox box);

}
