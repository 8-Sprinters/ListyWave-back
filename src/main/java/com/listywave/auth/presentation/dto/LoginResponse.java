package com.listywave.auth.presentation.dto;

import com.listywave.auth.application.dto.LoginResult;
import com.listywave.user.application.domain.User;
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

    public static LoginResponse of(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .id(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .backgroundImageUrl(user.getBackgroundImageUrl())
                .nickname(user.getNickname())
                .description(user.getDescription())
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .isFirst(false)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
