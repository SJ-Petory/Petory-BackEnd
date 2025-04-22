package com.sj.Petory.domain.caregiver.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.dto.CareGiverPetResponse;
import com.sj.Petory.domain.pet.entity.Breed;
import com.sj.Petory.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "caregiver")
public class CareGiver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "care_giver_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public static CareGiver toEntity(Member friend, Pet pet) {
        return CareGiver.builder()
                .member(friend)
                .pet(pet)
                .build();
    }

    public CareGiverPetResponse toDto(Breed breed) {
        Pet pet = this.getPet();
        return CareGiverPetResponse.builder()
                .petId(pet.getPetId())
                .name(pet.getPetName())
                .image(pet.getPetImage())
                .species(pet.getSpecies().getSpeciesName())
                .breed(breed.getBreedName())
                .age(pet.getPetAge())
                .gender(pet.getPetGender())
                .memo(pet.getMemo())
                .build();
    }
}
