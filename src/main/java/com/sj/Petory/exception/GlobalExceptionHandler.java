package com.sj.Petory.exception;

import com.sj.Petory.domain.member.controller.MemberController;
import com.sj.Petory.exception.dto.ErrorResponse;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.sj.Petory.exception.type.ErrorCode.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            CustomException e) {

        log.error("{} : {}", e.getClass().getName(), e.getErrorCode().getDescription());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ErrorResponse.from(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e) {

        log.error("{} : {}", e.getClass().getName(), e.getMessage());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ErrorResponse.from(e, INTERNAL_SERVER_ERROR));
    }
}
