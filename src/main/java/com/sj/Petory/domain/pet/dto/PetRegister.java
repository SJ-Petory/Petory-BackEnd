package com.sj.Petory.domain.pet.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.entity.Breed;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.entity.Species;
import com.sj.Petory.domain.pet.type.PetGender;
import com.sj.Petory.domain.pet.type.PetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class PetRegister {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotNull
        private long speciesId;

        @NotNull
        private long breedId;

        @NotBlank
        private String name;

        @NotNull
        private long age;

        @NotNull
        private PetGender gender;

        private String image;
        private String memo;

        public Pet toEntity(
                final Member member
                , final Species species
                , final Breed breed) {

            return Pet.builder()
                    .member(member)
                    .petName(this.getName())
                    .species(species)
                    .breed(breed.getBreedId())
                    .petGender(this.getGender())
                    .petAge(this.getAge())
                    .petImage(this.getImage())
                    .memo(this.getMemo())
                    .status(PetStatus.ACTIVE)
                    .build();
        }
    }
}
