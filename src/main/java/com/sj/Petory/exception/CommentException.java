package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class CommentException extends CustomException{
    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
