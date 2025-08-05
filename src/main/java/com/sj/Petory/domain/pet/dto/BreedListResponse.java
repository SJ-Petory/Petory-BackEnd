package com.sj.Petory.domain.pet.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreedListResponse {

    private Long breedId;
    private String breedName;
}
