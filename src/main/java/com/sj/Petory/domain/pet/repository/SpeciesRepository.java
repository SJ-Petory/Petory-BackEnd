package com.sj.Petory.domain.pet.repository;

import com.sj.Petory.domain.pet.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {

    Optional<Species> findBySpeciesId(Long speciesId);
}
