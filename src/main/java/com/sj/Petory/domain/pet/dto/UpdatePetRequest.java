package com.sj.Petory.domain.pet.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePetRequest {

    private String name;
    private long age;
    private MultipartFile image;
    private String memo;
}
