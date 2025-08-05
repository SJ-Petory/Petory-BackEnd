package com.sj.Petory.domain.member.dto;

import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.type.PetGender;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetResponse {

    private long petId;
    private String name;
    private String image;

    private String species;
    private String breed;
    private long age;
    private PetGender gender;
    private String memo;


}
