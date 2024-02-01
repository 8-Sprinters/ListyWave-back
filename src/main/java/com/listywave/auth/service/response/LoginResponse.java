package com.listywave.auth.service.response;

import com.listywave.user.domain.User;

public record LoginResponse(
        Long id,
        String profileImageUrl,
        String backgroundImageUrl,
        String nickname,
        String description,
        int followingCount,
        int followerCount,
        boolean isFirst
) {

    public static LoginResponse of(User user, boolean isFirst) {
        return new LoginResponse(
                user.getId(),
                user.getProfileImageUrl().getValue(),
                user.getBackgroundImageUrl().getValue(),
                user.getNickname().getValue(),
                user.getDescription().getValue(),
                user.getFollowingCount(),
                user.getFollowerCount(),
                isFirst
        );
    }
}
