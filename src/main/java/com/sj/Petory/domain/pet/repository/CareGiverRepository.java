package com.sj.Petory.domain.pet.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.entity.CareGiver;
import com.sj.Petory.domain.pet.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {
    Optional<CareGiver> findByPetAndMember(Pet pet, Member member);

    Page<CareGiver> findByMember(Member member, Pageable pageable);

    boolean existsByPetAndMember(Pet pet, Member member);
}
