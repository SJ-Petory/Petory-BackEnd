package com.sj.Petory.domain.friend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetInfo {

    private Long id;
    private String name;
    private String petImage;
}
