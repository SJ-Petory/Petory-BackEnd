package com.sj.Petory.domain.pet.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.type.PetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByMemberAndStatus(Member member, PetStatus status, Pageable pageable);

    Optional<Pet> findByPetIdAndMember(long petId, Member member);

    boolean existsByPetIdAndMember(Long petId, Member member);


    List<Pet> findByMember(Member member);
    @Query("select p.petId from Pet p" +
            " where p.member.id = :memberId")
    List<Long> findPetIdsByMember(@Param("memberId") Long memberId);

    Optional<Pet> findByPetId(Long petId);
}
