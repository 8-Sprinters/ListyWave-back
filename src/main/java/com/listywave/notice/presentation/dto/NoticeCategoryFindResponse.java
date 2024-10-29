package com.listywave.notice.presentation.dto;

import com.listywave.notice.application.domain.NoticeType;
import java.util.Arrays;
import java.util.List;

public record NoticeCategoryFindResponse(
        int code,
        String viewName
) {

    public static List<NoticeCategoryFindResponse> toList(NoticeType[] types) {
        return Arrays.stream(types)
                .map(type -> new NoticeCategoryFindResponse(type.getCode(), type.getViewName()))
                .toList();
    }
}
