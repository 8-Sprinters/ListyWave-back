package com.listywave.auth.application.dto;

import com.listywave.user.application.domain.User;

public record LoginResponse(
        Long id,
        String profileImageUrl,
        String backgroundImageUrl,
        String nickname,
        String description,
        boolean isFirst,
        String accessToken
) {

    public static LoginResponse of(User user, boolean isFirst, String accessToken) {
        return new LoginResponse(
                user.getId(),
                user.getProfileImageUrl(),
                user.getBackgroundImageUrl(),
                user.getNickname(),
                user.getDescription(),
                isFirst,
                accessToken
        );
    }
}
