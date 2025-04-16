package com.sj.Petory.domain.pet.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.entity.CareGiver;
import com.sj.Petory.domain.pet.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {
    Optional<CareGiver> findByPetAndMember(Pet pet, Member member);

    @Query("select case when exists (" +
            "select 1 from CareGiver cg " +
            "where cg.pet.petId = :petId and cg.member.memberId = :memberId) " +
            "then true " +
            "else false end")
    boolean existsByPetIdAndMember(
            @Param("petId")Long petId,
            @Param("memberId")Long memberId);

    Page<CareGiver> findByMember(Member member, Pageable pageable);

    boolean existsByPetAndMember(Pet pet, Member member);

    @Query("select cg.pet.id from CareGiver cg" +
            " where cg.member.id = :memberId")
    List<Long> findPetIdsByMember(@Param("memberId") Long memberId);

    @Query("select cg.member.memberId" +
            " from CareGiver cg" +
            " where cg.pet.petId in :petIds")
    List<Long> findMemberIdsByPet(@Param("petIds") List<Long> petIds);

    void deleteByPetAndMember(Pet pet, Member careGiver);
}
