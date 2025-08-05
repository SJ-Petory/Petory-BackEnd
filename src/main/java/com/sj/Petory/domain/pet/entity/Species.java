package com.sj.Petory.domain.pet.entity;

import com.sj.Petory.domain.pet.dto.SpeciesListResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "species_id")
    private Long speciesId;

    @Column(name = "species_name")
    private String speciesName;

    public SpeciesListResponse toListDto() {

        return SpeciesListResponse.builder()
                .speciesId(this.getSpeciesId())
                .speciesName(this.getSpeciesName())
                .build();
    }
}
