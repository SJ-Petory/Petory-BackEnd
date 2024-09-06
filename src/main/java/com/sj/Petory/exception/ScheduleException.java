package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class ScheduleException extends CustomException{
    public ScheduleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
