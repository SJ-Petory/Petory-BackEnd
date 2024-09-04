package com.sj.Petory.domain.pet.entity;

import com.sj.Petory.domain.member.dto.PetResponse;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.dto.UpdatePetRequest;
import com.sj.Petory.domain.pet.type.PetGender;
import com.sj.Petory.domain.pet.type.PetStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String petName;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(name = "breed_id")
    private Long breed;

    @Column(name = "pet_gender")
    @Enumerated(EnumType.STRING)
    private PetGender petGender;

    @Column
    private Long petAge;

    @Column(name = "pet_image")
    private String petImage;

    @Column
    private String memo;

    @Column
    @Enumerated(EnumType.STRING)
    private PetStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public PetResponse toDto(Breed breed) {
        return PetResponse.builder()
                .petId(this.getPetId())
                .name(this.getPetName())
                .image(this.getPetImage())
                .species(this.getSpecies().getSpeciesName())
                .breed(breed.getBreedName())
                .age(this.getPetAge())
                .gender(this.getPetGender())
                .memo(this.getMemo())
                .build();
    }

    public void updateInfo(final UpdatePetRequest request) {
        if (StringUtils.hasText(request.getName())) {
            this.petName = request.getName();
        }
        if (request.getAge() != 0) {
            this.petAge = request.getAge();
        }
        if (StringUtils.hasText(request.getImage())) {
            this.petImage = request.getImage();
        }
        if (StringUtils.hasText(request.getMemo())) {
            this.memo = request.getMemo();
        }
    }

    public void updateStatus(final PetStatus petStatus) {
        this.status = petStatus;
    }
}
