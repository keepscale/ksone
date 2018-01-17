package org.crossfit.app.repository;

import java.util.List;
import java.util.Set;

import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.enumeration.BillStatus;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Bill entity.
 */
public interface BillRepository extends JpaRepository<Bill,Long>, BillsBucket<Bill> {

    @Query(
    		"select b "
    		+ "from Bill b "
    		+ "where b.box =:box AND b.number like :year "
    		+ "order by b.number desc ")
	Page<Bill> findAllBillNumberLikeForBoxOrderByNumberDesc(@Param("year") String year, @Param("box") CrossFitBox box, Pageable pageable);

    static final String BILL_QUERY = 
    		"where b.box = :box "
    		+ "and ( "
    		
    		+ "	lower(m.firstName) like :search "
    		+ "	or lower(m.lastName) like :search "
    		+ "	or lower(m.telephonNumber) like :search "
    		+ "	or lower(m.login) like :search "
    		
    		+ "	or lower(b.number) like :search "
    		+ "	or lower(b.displayName) like :search "
    		+ "	or lower(b.displayAddress) like :search "
    		+ "	or lower(b.comments) like :search "
    		+ " or exists ( select line from BillLine line where line.bill = b and lower(line.label) like :search )"
    		+ ") "
    		+ "and ( "
    		+ "		(true = :includeAllStatus ) or b.status in (:includeStatus) "
    		+ ") ";
    @Query(
    		value="select b "
    				+ "from Bill b "
    	    		+ "left join fetch b.lines line "
    				+ "left join fetch line.subscription s "
    		 		+ "left join fetch s.membership ms "
    	    		+ "join fetch b.member m "
    	    		+ BILL_QUERY
    	    		+ "order by b.number desc ", 
    		countQuery = "select count(b) from Bill b  join b.member m " + BILL_QUERY)
	Page<Bill> findAll(
			@Param("box") CrossFitBox box, @Param("search") String search, 
			@Param("includeStatus") Set<BillStatus> includeStatus, 
			@Param("includeAllStatus") boolean includeAllStatus,
			Pageable pageable);

    @Query(
    		value="select b "
    				+ "from Bill b "
    	    		+ "left join fetch b.lines line "
    				+ "left join fetch line.subscription s "
    	    		+ "join fetch b.member m "
    	    		+ "where b.box=:box and (:start <= b.effectiveDate AND b.effectiveDate <= :end)")
	List<Bill> findAll(@Param("box") CrossFitBox box, @Param("start") LocalDate periodStart, @Param("end") LocalDate periodEnd);
    
    @Query(
    		value="select b "
    				+ "from Bill b "
    	    		+ "left join fetch b.lines line "
    				+ "left join fetch line.subscription s "
    		 		+ "left join fetch s.membership ms "
    	    		+ "join fetch b.member m "
    	    		+ "where b.id = :id and b.box=:box")
	Bill findOneWithEagerRelation(@Param("id") Long id, @Param("box") CrossFitBox box);

    @Modifying
	@Transactional
	@Query("delete from Bill b where b.status = :status and b.box=:box")
	void deleteBills(@Param("box") CrossFitBox box, @Param("status") BillStatus status);


    @Modifying
	@Transactional
	@Query("delete from BillLine line where exists (select b from Bill b where b.status = :status and b.box=:box and line.bill = b)")
	void deleteBillsLine(@Param("box") CrossFitBox box, @Param("status") BillStatus status);
    
    


}
