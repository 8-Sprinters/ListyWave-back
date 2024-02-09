package com.listywave.user.presentation.dto;

import com.listywave.user.application.dto.UserProflieUpdateCommand;

public record UserProfileUpdateRequest(
        String nickname,
        String description,
        String profileImageUrl,
        String backgroundImageUrl
) {

    public UserProflieUpdateCommand toCommand() {
        return UserProflieUpdateCommand.builder()
                .nickname(nickname)
                .description(description)
                .profileImageUrl(profileImageUrl)
                .backgroundImageUrl(backgroundImageUrl)
                .build();
    }
}
