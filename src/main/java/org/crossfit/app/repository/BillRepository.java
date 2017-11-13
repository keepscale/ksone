package org.crossfit.app.repository;

import java.util.List;
import java.util.Set;

import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.enumeration.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	List<Bill> findAllBillNumberLikeForBoxOrderByNumberDesc(@Param("year") String year, @Param("box") CrossFitBox box);

    
    @Query(
    		"select b "
    		+ "from Bill b "
    		+ "join b.member m "
    		+ "where b.box = :box "
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
    		+ ") "
    		+ "order by b.number desc ")
	Page<Bill> findAll(
			@Param("box") CrossFitBox box, @Param("search") String search, 
			@Param("includeStatus") Set<BillStatus> includeStatus, 
			@Param("includeAllStatus") boolean includeAllStatus,
			Pageable pageable);
}
