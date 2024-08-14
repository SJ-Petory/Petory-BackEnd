package com.sj.Petory.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    //Member
    EMAIL_DUPLICATED("중복된 이메일입니다.", HttpStatus.BAD_REQUEST),
    NAME_DUPLICATED("중복된 이름입니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_UNMATCHED("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED("만료된 토큰입니다.", HttpStatus.BAD_REQUEST);

    private final String description;
    private final HttpStatus httpStatus;
}

