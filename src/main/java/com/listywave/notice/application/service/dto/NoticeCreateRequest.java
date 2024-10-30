package com.listywave.notice.application.service.dto;

import com.listywave.notice.application.domain.ContentType;
import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeContent;
import com.listywave.notice.application.domain.NoticeDescription;
import com.listywave.notice.application.domain.NoticeTitle;
import com.listywave.notice.application.domain.NoticeType;
import jakarta.annotation.Nullable;
import java.util.List;

public record NoticeCreateRequest(
        int categoryCode,
        String title,
        String description,
        List<ContentDto> contents
) {

    public record ContentDto(
            int order,
            String type,
            @Nullable String description,
            @Nullable String imageUrl,
            @Nullable String buttonName,
            @Nullable String buttonLink
    ) {
    }

    public Notice toNotice() {
        return new Notice(NoticeType.codeOf(categoryCode), new NoticeTitle(title), new NoticeDescription(description));
    }

    public List<NoticeContent> toNoticeContents(Notice notice) {
        return contents.stream()
                .map(it -> NoticeContent.create(notice,
                        it.order,
                        ContentType.valueOf(it.type.toUpperCase()),
                        it.description,
                        it.imageUrl,
                        it.buttonName,
                        it.buttonLink)
                ).toList();
    }
}
