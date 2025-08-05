package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class NoticeException extends CustomException {
    public NoticeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
