package com.listywave.notice.application.service.dto;

import com.listywave.notice.application.domain.Notice;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record NoticeFindAllResponseToAdmin(
        Long id,
        LocalDateTime createdDate,
        String title,
        String category,
        String description,
        boolean isExposed,
        boolean didSendAlarm
) {

    public static List<NoticeFindAllResponseToAdmin> toList(List<Notice> notices) {
        return notices.stream()
                .map(NoticeFindAllResponseToAdmin::of)
                .toList();
    }

    public static NoticeFindAllResponseToAdmin of(Notice notice) {
        return NoticeFindAllResponseToAdmin.builder()
                .id(notice.getId())
                .createdDate(notice.getCreatedDate())
                .title(notice.getTitle().getValue())
                .category(notice.getType().getViewName())
                .description(notice.getDescription().getValue())
                .isExposed(notice.isExposed())
                .didSendAlarm(notice.isDidSendAlarm())
                .build();
    }
}
