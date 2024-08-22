package com.sj.Petory.OAuth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraUserInfo {

    @NotBlank
    private String email;
    @NotBlank
    private String phone;
}
