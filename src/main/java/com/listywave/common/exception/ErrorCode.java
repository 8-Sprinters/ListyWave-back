package com.listywave.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 한 값 타입이 잘못되어 binding에 실패하였습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP method 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류, 관리자에게 문의하세요"),

    NOT_SUPPORTED_OAUTH_SERVER(HttpStatus.BAD_REQUEST, "지원하지 않는 OAuth 서버입니다."),

    // Validation
    NICKNAME_CONTAINS_WHITESPACE(HttpStatus.BAD_REQUEST, "닉네임의 처음과 마지막에 공백이 존재할 수 없습니다."),
    NICKNAME_CONTAINS_SPECIAL_CHARACTERS(HttpStatus.BAD_REQUEST, "이모티콘 및 특수문자가 포함될 수 없습니다."),
    LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "최대 글자 길이를 초과하였습니다."),
    INVALID_COUNT(HttpStatus.BAD_REQUEST, "선택한 아이템 및 라벨의 개수가 올바르지 않습니다."),
    NOT_FOUND(HttpStatus.BAD_REQUEST, "대상이 존재하지 않습니다."),

    REQUIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "인증 정보가 필요합니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 AccessToken 입니다. 다시 로그인해주세요."),

    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "중복된 사용자를 선택할 수 없습니다."),

    // list
    INVALID_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 존재하지 않습니다."),

    // S3
    S3_DELETE_OBJECTS_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류, 관리자에게 문의하세요"),
    ;

    private final HttpStatus status;
    private final String detail;
}
