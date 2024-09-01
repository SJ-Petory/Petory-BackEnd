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
    TOKEN_EXPIRED("만료된 토큰입니다.", HttpStatus.BAD_REQUEST),

    ALREADY_DELETED_MEMBER("이미 탈퇴한 회원입니다.", HttpStatus.BAD_REQUEST),

    //friend
    ALREADY_FRIEND_MEMBER("이미 친구인 사용자 입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_FRIEND_REQUEST("이미 요청보낸 사용자 입니다.", HttpStatus.BAD_REQUEST),
    REQUEST_MYSELF_NOT_ALLOWED("자기 자신에게 요청은 불가합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_RECEIVE_FRIEND_REQUEST("이미 상대방이 친구요청을 보냈습니다.", HttpStatus.BAD_REQUEST),
    REQUEST_NOT_FOUND("요청을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    STATUS_NOT_ALLOWED("올바르지 않은 상태값입니다.", HttpStatus.BAD_REQUEST),

    //s3
    FILE_EMPTY("유효하지 않은 파일입니다.", HttpStatus.BAD_REQUEST),
    FILE_EXTENSION_NOT_ALLOWED("이미지 파일만 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAIL("이미지 업로드가 실패 하였습니다.", HttpStatus.BAD_REQUEST);

    private final String description;
    private final HttpStatus httpStatus;
}

