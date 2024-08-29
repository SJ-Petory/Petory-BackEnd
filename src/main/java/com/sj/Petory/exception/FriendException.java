package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class FriendException extends CustomException{
    public FriendException(ErrorCode errorCode) {
        super(errorCode);
    }
}
