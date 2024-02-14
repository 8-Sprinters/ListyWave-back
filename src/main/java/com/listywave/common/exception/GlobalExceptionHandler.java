package com.listywave.common.exception;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS_TOKEN;
import static com.listywave.common.exception.ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("[CustomException] : {}", e.getMessage(), e);
        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Void> handleException(Exception e) {
        log.error("[InternalServerError] : {}", e.getMessage(), e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("[MethodArgumentTypeMismatchException] : {}", e.getMessage(), e);
        CustomException customException = new CustomException(METHOD_ARGUMENT_TYPE_MISMATCH);
        return ErrorResponse.toResponseEntity(customException);
    }

    @ExceptionHandler(SignatureException.class)
    ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
        log.error("[SignatureException] : {}", e.getMessage(), e);
        CustomException customException = new CustomException(INVALID_ACCESS_TOKEN);
        return ErrorResponse.toResponseEntity(customException);
    }
}
