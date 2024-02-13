package com.listywave.user.application.dto;

import com.listywave.user.application.domain.User;
import lombok.Builder;

@Builder
public record UserInfoResponse(
        Long id,
        String backgroundImageUrl,
        String profileImageUrl,
        String nickname,
        String description,
        int followingCount,
        int followerCount,
        boolean isFollowed,
        boolean isOwner
) {

    public static UserInfoResponse of(User user, boolean isFollowed, boolean isOwner) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .backgroundImageUrl(user.getBackgroundImageUrl())
                .profileImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .description(user.getDescription())
                .followingCount(user.getFollowingCount())
                .followerCount(user.getFollowerCount())
                .isFollowed(isFollowed)
                .isOwner(isOwner)
                .build();
    }
}
