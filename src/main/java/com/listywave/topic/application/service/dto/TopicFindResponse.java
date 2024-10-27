package com.listywave.topic.application.service.dto;

import com.listywave.topic.application.domain.Topic;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record TopicFindResponse(
        boolean hasNext,
        long totalCount,
        Long cursorId,
        List<TopicDto> topics
) {

    public static TopicFindResponse from(List<Topic> topics, int size, long totalCount) {
        if (topics.isEmpty()) {
            return new TopicFindResponse(false, totalCount, null, List.of());
        }

        boolean hasNext = false;
        if (topics.size() > size) {
            hasNext = true;
            topics.remove(topics.size() - 1);
        }
        long cursorId = topics.get(topics.size() - 1).getId();

        return TopicFindResponse.builder()
                .hasNext(hasNext)
                .totalCount(totalCount)
                .cursorId(cursorId)
                .topics(TopicDto.toList(topics))
                .build();
    }

    @Builder
    public record TopicDto(
            String categoryEngName,
            String categoryKorName,
            String title,
            String description,
            LocalDateTime createdDate,
            Long ownerId,
            String ownerNickname,
            boolean isAnonymous,
            boolean isExposed
    ) {

        public static List<TopicDto> toList(List<Topic> topics) {
            return topics.stream()
                    .map(TopicDto::of)
                    .toList();
        }

        private static TopicDto of(Topic topic) {
            return TopicDto.builder()
                    .categoryEngName(topic.getCategory().name())
                    .categoryKorName(topic.getCategory().getViewName())
                    .title(topic.getTitle().getValue())
                    .description(topic.getDescription().getValue())
                    .createdDate(topic.getCreatedDate())
                    .ownerId(topic.getUser().getId())
                    .ownerNickname(topic.getUser().getNickname())
                    .isAnonymous(topic.isAnonymous())
                    .isExposed(topic.isExposed())
                    .build();
        }
    }
}
