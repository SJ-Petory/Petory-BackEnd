package com.sj.Petory.domain.pet.dto;

public record CreateBreedRequest(
        Long speciesId,
        String name
) {
}
