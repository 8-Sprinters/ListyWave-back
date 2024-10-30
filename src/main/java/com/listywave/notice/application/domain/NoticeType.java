package com.listywave.notice.application.domain;

import static com.listywave.common.exception.ErrorCode.NOT_EXIST_CODE;

import com.listywave.common.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeType {

    NEWS(1, "소식"),
    EVENT(2, "이벤트"),
    TIP(3, "팁"),
    ;

    private final int code;
    private final String viewName;

    public static NoticeType codeOf(int code) {
        return Arrays.stream(NoticeType.values())
                .filter(noticeType -> noticeType.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new CustomException(NOT_EXIST_CODE, NOT_EXIST_CODE.getDetail() + " 입력값: " + code));
    }
}
