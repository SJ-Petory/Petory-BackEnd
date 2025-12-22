package com.sj.Petory.OAuth.dto;


public record TokenInfo (
        String accessToken,
        String refreshToken
){
}
