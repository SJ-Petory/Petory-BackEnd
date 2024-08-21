package com.sj.Petory.OAuth;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoExtraUserInfo {
    private String email;
    private String phone;
}
