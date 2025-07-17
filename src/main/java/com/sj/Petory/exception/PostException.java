package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class PostException extends CustomException{
    public PostException(ErrorCode errorCode) {
        super(errorCode);
    }
}
