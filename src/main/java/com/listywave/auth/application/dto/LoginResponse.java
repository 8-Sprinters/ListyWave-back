package com.listywave.auth.application.dto;

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

    public static LoginResponse of(User user, String accessToken, String refreshToken, boolean isFirst) {
        return LoginResponse.builder()
                .id(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .backgroundImageUrl(user.getBackgroundImageUrl())
                .nickname(user.getNickname())
                .description(user.getDescription())
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .isFirst(isFirst)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
