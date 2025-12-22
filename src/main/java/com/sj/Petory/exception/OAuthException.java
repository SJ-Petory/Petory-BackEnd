package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class OAuthException extends CustomException{
    public OAuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
