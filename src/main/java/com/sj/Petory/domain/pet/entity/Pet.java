package com.sj.Petory.domain.pet.entity;

import com.sj.Petory.domain.member.dto.PetResponse;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.dto.PetRegister;
import com.sj.Petory.domain.pet.type.PetBreed;
import com.sj.Petory.domain.pet.type.PetGender;
import com.sj.Petory.domain.pet.type.PetSpecies;
import com.sj.Petory.domain.pet.type.PetStatus;
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
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String petName;

    @Column
    @Enumerated(EnumType.STRING)
    private PetSpecies species;

    @Column
    @Enumerated(EnumType.STRING)
    private PetBreed breed;

    @Column
    @Enumerated(EnumType.STRING)
    private PetGender petGender;

    @Column
    private Long petAge;

    @Column
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

    public PetResponse toDto() {
        return PetResponse.builder()
                .petId(this.getPetId())
                .name(this.getPetName())
                .image(this.getPetImage())
                .build();
    }
}
