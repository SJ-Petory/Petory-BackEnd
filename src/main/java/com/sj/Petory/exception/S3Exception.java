package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class S3Exception extends CustomException{
    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}
