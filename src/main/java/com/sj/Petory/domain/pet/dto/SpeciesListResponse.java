package com.sj.Petory.domain.pet.dto;

import lombok.*;

import javax.annotation.Nonnull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeciesListResponse {

    private long speciesId;
    private String speciesName;
}
