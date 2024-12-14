package com.listywave.topic.application.service.dto;

import com.listywave.topic.application.domain.Topic;
import java.util.List;

public record RecommendTopicFindResponse(
        Long id,
        String title
) {

    public static List<RecommendTopicFindResponse> toList(List<Topic> topics) {
        return topics.stream()
                .map(topic -> new RecommendTopicFindResponse(topic.getId(), topic.getTitle().getValue()))
                .toList();
    }
}
