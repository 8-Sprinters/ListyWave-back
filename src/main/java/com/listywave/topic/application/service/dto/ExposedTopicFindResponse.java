package com.listywave.topic.application.service.dto;

import com.listywave.topic.application.domain.Topic;
import java.util.List;
import lombok.Builder;

public record ExposedTopicFindResponse(
        Long cursorId,
        boolean hasNext,
        List<TopicDto> topics
) {

    public static ExposedTopicFindResponse of(List<Topic> topics, int size) {
        if (topics.isEmpty()) {
            return new ExposedTopicFindResponse(null, false, List.of());
        }

        boolean hasNext = false;
        if (topics.size() > size) {
            hasNext = true;
            topics.remove(topics.size() - 1);
        }
        Long cursorId = topics.get(topics.size() - 1).getId();
        List<TopicDto> topicDtos = TopicDto.toList(topics);

        return new ExposedTopicFindResponse(cursorId, hasNext, topicDtos);
    }

    @Builder
    public record TopicDto(
            Long id,
            String categoryEngName,
            String categoryKorName,
            String title,
            String description,
            Long ownerId,
            String ownerNickname,
            boolean isAnonymous
    ) {

        public static List<TopicDto> toList(List<Topic> topics) {
            return topics.stream()
                    .map(TopicDto::of)
                    .toList();
        }

        public static TopicDto of(Topic topic) {
            return TopicDto.builder()
                    .id(topic.getId())
                    .categoryEngName(topic.getCategory().name())
                    .categoryKorName(topic.getCategory().getViewName())
                    .title(topic.getTitle().getValue())
                    .description(topic.getDescription().getValue())
                    .ownerId(topic.getUser().getId())
                    .ownerNickname(topic.getUser().getNickname())
                    .isAnonymous(topic.isAnonymous())
                    .build();
        }
    }
}
