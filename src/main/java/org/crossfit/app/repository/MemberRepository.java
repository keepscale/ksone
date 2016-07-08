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

    @Query("select m from Member m where m.box = :box order by m.enabled DESC, m.lastName, m.firstName")
	Page<Member> findAll(@Param("box") CrossFitBox box, Pageable pageable);

    @Query("select m from Member m where m.login = :login and m.box = :box")
    Optional<Member> findOneByLogin(@Param("login") String login, @Param("box") CrossFitBox currentCrossFitBox);

    @Query("select m from Member m where m.box = :box and m.enabled = false")
    List<Member> findAllUserNotEnabled(@Param("box") CrossFitBox box);
    

}
