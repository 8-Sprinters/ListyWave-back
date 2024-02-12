package com.listywave.user.application.dto;

import lombok.Builder;

@Builder
public record UserProflieUpdateCommand(
        String nickname,
        String description,
        String profileImageUrl,
        String backgroundImageUrl
) {
}
