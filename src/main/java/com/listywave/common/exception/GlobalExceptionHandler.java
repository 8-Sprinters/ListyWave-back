package com.listywave.common.exception;


import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * CustomException 예외 처리
     */
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error(e.getMessage());
        return ErrorResponse.toResponseEntity(e);
    }

    /**
     * 500번대 에러 처리
     */
//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
//        log.error(e.getMessage());
//        CustomException ex = new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//        return ErrorResponse.toResponseEntity(ex);
//    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.error(e.getMessage());
        CustomException ex = new CustomException(ErrorCode.METHOD_NOT_ALLOWED);
        ErrorCode errorCode = ex.getErrorCode();
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

    /**
     * PathVariable, RequestParam, RequestHeader, RequestBody 에서 타입이 일치하지 않을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage());
        CustomException ex = new CustomException(ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH);
        return ErrorResponse.toResponseEntity(ex);
    }

    /**
     * JWT 서명 확인에 실패했을 경우 발생
     */
    @ExceptionHandler(SignatureException.class)
    ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
        log.error(e.getMessage());
        CustomException customException = new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        return ErrorResponse.toResponseEntity(customException);
    }
}
