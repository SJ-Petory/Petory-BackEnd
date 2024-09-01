package com.sj.Petory.domain.friend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDetailResponse {

    private Long id;
    private String name;
    private String image;

    private List<PetInfo> myPets;
    private List<PetInfo> careGivePets;
}
