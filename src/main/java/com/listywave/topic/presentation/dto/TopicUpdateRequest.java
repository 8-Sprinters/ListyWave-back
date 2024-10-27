package com.listywave.topic.presentation.dto;

public record TopicUpdateRequest(
        boolean isExposed,
        String categoryCode,
        String title
) {
}
