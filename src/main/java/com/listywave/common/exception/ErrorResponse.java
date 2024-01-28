package com.listywave.common.exception;

import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder
public record ErrorResponse(

    int status,
    String error,
    String code,
    String detail,
    String message
) {
    public static ResponseEntity<Object> toResponseEntity(CustomException e){
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(
                    ErrorResponse.builder()
                            .status(errorCode.getStatus().value())
                            .error(errorCode.getStatus().name())
                            .code(errorCode.name())
                            .detail(errorCode.getDetail())
                            .message(e.getMessage())
                            .build()
                );
    }
}
