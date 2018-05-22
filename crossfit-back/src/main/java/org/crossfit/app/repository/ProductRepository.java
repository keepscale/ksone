package org.crossfit.app.repository;

import java.util.List;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the Membership entity.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("select p from Product p where p.box = :box order by p.name")
	List<Product> findAll(@Param("box") CrossFitBox box);

	Product findOneByNameAndBox(String name, CrossFitBox box);

	Product findOneByIdAndBox(Long id, CrossFitBox box);

	@Modifying
	@Transactional
	@Query("delete from Product p where p.box = :box and p.id=:id")
	void delete(@Param("id") Long id, @Param("box") CrossFitBox box);
	

}
