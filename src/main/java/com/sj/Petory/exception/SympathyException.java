package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class SympathyException extends CustomException{
    public SympathyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
