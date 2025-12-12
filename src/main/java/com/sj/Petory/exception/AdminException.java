package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class AdminException extends CustomException {
    public AdminException(ErrorCode errorCode) {
        super(errorCode);
    }
}
