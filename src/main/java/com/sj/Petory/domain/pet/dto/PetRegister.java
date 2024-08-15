package com.sj.Petory.domain.pet.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.type.PetBreed;
import com.sj.Petory.domain.pet.type.PetGender;
import com.sj.Petory.domain.pet.type.PetSpecies;
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

        @NotBlank
        private String species;

        @NotBlank
        private String breed;

        @NotBlank
        private String name;

        @NotNull
        private long age;

        @NotBlank
        private String gender;

        private String image;
        private String memo;

        public Pet toEntity(Member member) {
            return Pet.builder()
                    .member(member)
                    .petName(this.getName())
                    .species(PetSpecies.valueOf(this.getSpecies()))
                    .breed(PetBreed.valueOf(this.getBreed()))
                    .petGender(PetGender.valueOf(this.getGender()))
                    .petAge(this.getAge())
                    .petImage(this.getImage())
                    .memo(this.getMemo())
                    .status(PetStatus.ACTIVE)
                    .build();
        }
    }
}
