package com.sj.Petory.domain.pet.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.type.PetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByMemberAndStatus(Member member, PetStatus status, Pageable pageable);

    Optional<Pet> findByPetIdAndMember(long petId, Member member);

    boolean existsByPetIdAndMember(Long petId, Member member);
}
