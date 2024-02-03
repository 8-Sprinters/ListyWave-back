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
        int followerCount,
        int followingCount,
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
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .isFollowed(isFollowed)
                .isOwner(isOwner)
                .build();
    }
}
