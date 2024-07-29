package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;
import org.springframework.http.HttpStatus;

public class MemberException extends CustomException{

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
