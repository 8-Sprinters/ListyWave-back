package com.listywave.common.exception;

import org.springframework.http.ResponseEntity;

public record ErrorResponse(
        int status,
        String error,
        String code,
        String detail,
        String message
) {

    public static ResponseEntity<ErrorResponse> toResponseEntity(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getStatus().name(),
                errorCode.name(),
                errorCode.getDetail(),
                e.getMessage()
        );
        
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }
}
