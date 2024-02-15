package com.listywave.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Auth
    NOT_SUPPORTED_OAUTH_SERVER(BAD_REQUEST, "지원하지 않는 OAuth 서버입니다."),
    REQUIRED_ACCESS_TOKEN(UNAUTHORIZED, "인증 정보가 필요합니다."),
    INVALID_ACCESS_TOKEN(UNAUTHORIZED, "유효하지 않은 AccessToken 입니다. 다시 로그인해주세요."),
    INVALID_ACCESS(FORBIDDEN, "접근 권한이 존재하지 않습니다."),
    CANNOT_COLLECT_OWN_LIST(BAD_REQUEST, "리스트 작성자는 자신의 리스트에 콜렉트할 수 없습니다."),

    // Http Request
    METHOD_ARGUMENT_TYPE_MISMATCH(BAD_REQUEST, "요청 한 값 타입이 잘못되어 binding에 실패하였습니다."),
    RESOURCE_NOT_FOUND(NOT_FOUND, "대상이 존재하지 않습니다."),

    // Validation
    NICKNAME_CONTAINS_WHITESPACE(BAD_REQUEST, "닉네임의 처음과 마지막에 공백이 존재할 수 없습니다."),
    NICKNAME_CONTAINS_SPECIAL_CHARACTERS(BAD_REQUEST, "이모티콘 및 특수문자가 포함될 수 없습니다."),
    LENGTH_EXCEEDED(BAD_REQUEST, "최대 글자 길이를 초과하였습니다."),
    INVALID_COUNT(BAD_REQUEST, "선택한 아이템 및 라벨의 개수가 올바르지 않습니다."),
    DUPLICATE_USER(BAD_REQUEST, "중복된 사용자를 선택할 수 없습니다."),

    // S3
    S3_DELETE_OBJECTS_EXCEPTION(INTERNAL_SERVER_ERROR, "S3의 이미지를 삭제 요청하는 과정에서 에러가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final String detail;
}
