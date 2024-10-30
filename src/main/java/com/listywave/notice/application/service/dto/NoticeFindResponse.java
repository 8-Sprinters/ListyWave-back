package com.listywave.notice.application.service.dto;

import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeContent;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record NoticeFindResponse(
        Long id,
        String category,
        String title,
        String description,
        List<ContentDto> contents,
        LocalDateTime createdDate,
        BesideNoticeDto prevNotice,
        BesideNoticeDto nextNotice
) {

    public static NoticeFindResponse of(Notice notice, Notice prevNotice, Notice nextNotice) {
        return NoticeFindResponse.builder()
                .id(notice.getId())
                .category(notice.getType().getViewName())
                .title(notice.getTitle().getValue())
                .description(notice.getDescription().getValue())
                .contents(ContentDto.toList(notice.getContents()))
                .createdDate(notice.getCreatedDate())
                .prevNotice(BesideNoticeDto.of(prevNotice))
                .nextNotice(BesideNoticeDto.of(nextNotice))
                .build();
    }

    @Builder
    public record ContentDto(
            String type,
            String description,
            @Nullable String imageUrl,
            @Nullable String buttonName,
            @Nullable String buttonLink
    ) {

        public static List<ContentDto> toList(List<NoticeContent> contents) {
            return contents.stream()
                    .map(ContentDto::of)
                    .toList();
        }

        public static ContentDto of(NoticeContent content) {
            return ContentDto.builder()
                    .type(content.getType().name().toLowerCase())
                    .description(content.getDescription())
                    .imageUrl(content.getImageUrl())
                    .buttonName(content.getButtonName())
                    .buttonLink(content.getButtonLink())
                    .build();
        }
    }

    public record BesideNoticeDto(
            Long id,
            String title,
            String description
    ) {

        public static BesideNoticeDto of(Notice notice) {
            if (notice == null) {
                return null;
            }
            return new BesideNoticeDto(notice.getId(), notice.getTitle().getValue(), notice.getDescription().getValue());
        }
    }
}
