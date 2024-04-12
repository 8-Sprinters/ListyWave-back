package com.listywave.auth.application.dto;

import com.listywave.user.application.domain.User;
import java.util.concurrent.TimeUnit;

public record LoginResult(
        Long id,
        String profileImageUrl,
        String backgroundImageUrl,
        String nickname,
        String description,
        int followingCount,
        int followerCount,
        boolean isFirst,
        String accessToken,
        String refreshToken,
        int accessTokenValidTimeDuration,
        int refreshTokenValidTimeDuration,
        TimeUnit accessTokenValidTimeUnit,
        TimeUnit refreshTokenValidTimeUnit
) {

    public static LoginResult of(
            User user,
            boolean isFirst,
            String accessToken,
            String refreshToken,
            int accessTokenValidTimeDuration,
            int refreshTokenValidTimeDuration,
            TimeUnit accessTokenValidTimeUnit,
            TimeUnit refreshTokenValidTimeUnit
    ) {
        return new LoginResult(
                user.getId(),
                user.getProfileImageUrl(),
                user.getBackgroundImageUrl(),
                user.getNickname(),
                user.getDescription(),
                user.getFollowingCount(),
                user.getFollowerCount(),
                isFirst,
                accessToken,
                refreshToken,
                accessTokenValidTimeDuration,
                refreshTokenValidTimeDuration,
                accessTokenValidTimeUnit,
                refreshTokenValidTimeUnit
        );
    }
}
