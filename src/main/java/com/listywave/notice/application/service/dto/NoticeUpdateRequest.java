package com.listywave.notice.application.service.dto;

import com.listywave.notice.application.domain.ContentType;
import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeContent;
import java.util.List;

public record NoticeUpdateRequest(
        int categoryCode,
        String title,
        String description,
        List<ContentDto> contents
) {


    public List<NoticeContent> toNoticeContents(Notice notice) {
        return contents.stream()
                .map(it -> NoticeContent.create(
                                notice,
                                it.order,
                                ContentType.valueOf(it.type.toUpperCase()),
                                it.description,
                                it.imageUrl,
                                it.buttonName,
                                it.buttonLink
                        )
                ).toList();
    }

    public record ContentDto(
            int order,
            String type,
            String description,
            String imageUrl,
            String buttonName,
            String buttonLink
    ) {
    }
}
