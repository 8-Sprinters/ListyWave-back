package com.listywave.notice.application.service.dto;

import com.listywave.notice.application.domain.Notice;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record NoticeFindAllResponseToUser(
        Long id,
        LocalDateTime createdDate,
        String title,
        @Nullable String itemImageUrl,
        String category,
        String description
) {

    public static List<NoticeFindAllResponseToUser> toList(List<Notice> notices) {
        return notices.stream()
                .map(NoticeFindAllResponseToUser::of)
                .toList();
    }

    public static NoticeFindAllResponseToUser of(Notice notice) {
        return NoticeFindAllResponseToUser.builder()
                .id(notice.getId())
                .createdDate(notice.getCreatedDate())
                .title(notice.getTitle().getValue())
                .itemImageUrl(notice.getFirstImageUrl())
                .category(notice.getType().getViewName())
                .description(notice.getDescription().getValue())
                .build();
    }
}
