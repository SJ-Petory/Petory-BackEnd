package com.sj.Petory.domain.pet.entity;

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
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breed_id")
    private Long breedId;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(name = "breed_name")
    private String breedName;
}
