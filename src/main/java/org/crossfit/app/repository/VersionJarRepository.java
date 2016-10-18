package org.crossfit.app.repository;

import java.util.Optional;

import org.crossfit.app.domain.VersionJar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the {@link VersionJar} entity.
 */
public interface VersionJarRepository extends JpaRepository<VersionJar,Long> {

	@Query("from VersionJar where actif = true")
	Optional<VersionJar> findOneActif();
	
}
