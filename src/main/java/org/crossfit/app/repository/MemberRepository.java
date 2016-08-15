package org.crossfit.app.repository;

import java.util.List;
import java.util.Optional;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Member entity.
 */
public interface MemberRepository extends JpaRepository<Member,Long> {

    @Query("select m from Member m where m.box = :box "
    		+ "and ( "
    		+ "	lower(m.firstName) like :search "
    		+ "	or lower(m.lastName) like :search "
    		+ "	or lower(m.telephonNumber) like :search "
    		+ "	or lower(m.login) like :search "
    		+ ") "
    		+ "and ( "
    		+ "( true = :includeActif AND m.enabled = true and m.locked = false ) "
    		+ "or ( true = :includeNotEnabled and m.enabled = false ) "
    		+ "or ( true = :includeBloque and m.locked = true ) "
    		+ ") "
    		+ "order by m.enabled DESC, m.lastName, m.firstName")
	List<Member> findAll(@Param("box") CrossFitBox box, @Param("search") String search, 
			@Param("includeActif") boolean includeActif,@Param("includeNotEnabled")boolean includeNotEnabled, @Param("includeBloque")boolean includeBloque, 
			Pageable pageable);

    @Query("select m from Member m left join fetch m.authorities where m.login = :login and m.box = :box")
    Optional<Member> findOneByLogin(@Param("login") String login, @Param("box") CrossFitBox currentCrossFitBox);

    @Query("select m from Member m where m.box = :box and m.enabled = false")
    List<Member> findAllUserNotEnabled(@Param("box") CrossFitBox box);
    

}
