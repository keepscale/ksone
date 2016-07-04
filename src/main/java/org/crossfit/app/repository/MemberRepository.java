package org.crossfit.app.repository;

import java.util.List;

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

    @Query("select m from Member m where m.user.login = ?#{principal.username}")
    List<Member> findByUserIsCurrentUser();

    @Query("select m from Member m where m.box = :box order by m.user.activated DESC, m.user.activationKey DESC, m.user.lastName, m.user.firstName")
	Page<Member> findAll(@Param("box") CrossFitBox box, Pageable pageable);

    @Query("select m from Member m where m.id = :id and m.box = :box")
	Member findOne(@Param("id") Long id, @Param("box") CrossFitBox currentCrossFitBox);

    @Query("select m from Member m where m.user.login = :login")
    Member findOneByLogin(@Param("login") String login);

    @Query("select m from Member m where m.user.login = :login and m.box = :box")
    Member findOneByLogin(@Param("login") String login, @Param("box") CrossFitBox currentCrossFitBox);

    @Query("select m from Member m where m.box = :box and m.user.activated = false")
    List<Member> findAllUserNotActivated(@Param("box") CrossFitBox box);
    

}
