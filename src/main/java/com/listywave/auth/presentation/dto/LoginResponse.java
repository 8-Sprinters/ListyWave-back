package com.listywave.auth.presentation.dto;

import com.listywave.auth.application.dto.LoginResult;
import lombok.Builder;

@Builder
public record LoginResponse(
        Long id,
        String profileImageUrl,
        String backgroundImageUrl,
        String nickname,
        String description,
        int followingCount,
        int followerCount,
        boolean isFirst,
        String accessToken,
        String refreshToken
) {

    public static LoginResponse of(LoginResult result) {
        return LoginResponse.builder()
                .id(result.id())
                .profileImageUrl(result.profileImageUrl())
                .backgroundImageUrl(result.backgroundImageUrl())
                .nickname(result.nickname())
                .description(result.description())
                .followerCount(result.followerCount())
                .followingCount(result.followingCount())
                .isFirst(result.isFirst())
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .build();
    }
}
