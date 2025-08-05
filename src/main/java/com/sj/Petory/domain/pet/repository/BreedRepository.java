package com.sj.Petory.domain.pet.repository;

import com.sj.Petory.domain.pet.entity.Breed;
import com.sj.Petory.domain.pet.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
    Optional<Breed> findByBreedId(Long breedId);

    List<Breed> findBySpecies(Species species);
}
