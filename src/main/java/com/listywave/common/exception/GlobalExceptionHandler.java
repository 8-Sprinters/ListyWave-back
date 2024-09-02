package com.listywave.common.exception;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS_TOKEN;
import static com.listywave.common.exception.ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        String message = e.getErrorCode().getDetail();
        log.error("[CustomException] : {}", message, e);
        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> handleException(Exception e) {
        log.error("[InternalServerError] : {}", e.getMessage(), e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("[IllegalArgumentException] : {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
        CustomException customException = new CustomException(INVALID_ACCESS_TOKEN, "서명 값이 잘못된 액세스 토큰입니다.");
        return ErrorResponse.toResponseEntity(customException);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("[ExpiredJwtException] : {}", e.getMessage(), e);
        CustomException customException = new CustomException(INVALID_ACCESS_TOKEN, "만료된 액세스 토큰입니다.");
        return ErrorResponse.toResponseEntity(customException);
    }

    @ExceptionHandler(MalformedJwtException.class)
    ResponseEntity<Void> handleMalformedJwtException(MalformedJwtException e) {
        log.error("[MalformedJwtException] : {}", e.getMessage(), e);
        return ResponseEntity.status(UNAUTHORIZED).build();
    }
}
