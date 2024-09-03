package com.sj.Petory.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePetRequest {

    private String name;
    private long age;
    private String image;
    private String memo;
}
