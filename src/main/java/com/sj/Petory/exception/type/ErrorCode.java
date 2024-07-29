package com.sj.Petory.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    //Member
    EMAIL_DUPLICATED("중복된 이메일입니다.", HttpStatus.BAD_REQUEST),
    NAME_DUPLICATED("중복된 이름입니다.", HttpStatus.BAD_REQUEST);

    private final String description;
    private final HttpStatus httpStatus;
}

