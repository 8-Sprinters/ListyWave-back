package com.listywave.common.exception;

import org.springframework.http.ResponseEntity;

public record ErrorResponse(
    int status,
    String error,
    String code,
    String detail,
    String message
) {

    public static ResponseEntity<ErrorResponse> toResponseEntity(CustomException e){
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(
                        new ErrorResponse(
                                errorCode.getStatus().value(),
                                errorCode.getStatus().name(),
                                errorCode.name(),
                                errorCode.getDetail(),
                                e.getMessage()
                        )
                );
    }
}
