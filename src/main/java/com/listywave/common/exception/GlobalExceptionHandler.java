package com.listywave.common.exception;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS_TOKEN;
import static com.listywave.common.exception.ErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
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
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("[IllegalArgumentException] : {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.error("[MethodArgumentNotValidException] : {}", e.getMessage(), e);
        String errorMessage = e.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ErrorCode errorCode = METHOD_ARGUMENT_NOT_VALID_EXCEPTION;
        ErrorResponse errorResponse = new ErrorResponse(status.value(), errorMessage, errorCode.name(), errorCode.getDetail(), errorMessage);
        return ResponseEntity.badRequest().body(errorResponse);
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

    @ExceptionHandler(NullPointerException.class)
    ResponseEntity<String> handleNullPointerException(NullPointerException e) {
        log.error("[NullPointerException] : {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body("NullPointException이 발생했습니다. " + e.getMessage());
    }
}
