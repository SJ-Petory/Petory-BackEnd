package com.sj.Petory.exception.dto;

import com.sj.Petory.exception.CustomException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;

    public static ErrorResponse from(
            final Exception e, final ErrorCode errorCode) {

        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(e.getMessage())
                .httpStatus(errorCode.getHttpStatus())
                .build();
    }

    public static ErrorResponse from(final CustomException e) {
        return ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorCode().getDescription())
                .httpStatus(e.getErrorCode().getHttpStatus())
                .build();
    }
}
