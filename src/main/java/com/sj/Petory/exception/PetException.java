package com.sj.Petory.exception;

import com.sj.Petory.exception.type.ErrorCode;

public class PetException extends CustomException{
    public PetException(ErrorCode errorCode) {
        super(errorCode);
    }
}
