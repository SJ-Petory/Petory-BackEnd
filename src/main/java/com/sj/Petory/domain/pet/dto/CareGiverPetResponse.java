package com.sj.Petory.domain.pet.dto;

import com.sj.Petory.domain.pet.type.PetGender;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareGiverPetResponse {
    private long petId;
    private String name;
    private String image;

    private String species;
    private String breed;
    private long age;
    private PetGender gender;
    private String memo;

}
