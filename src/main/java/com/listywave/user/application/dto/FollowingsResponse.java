package com.listywave.user.application.dto;

import com.listywave.user.application.domain.User;
import java.util.List;
import lombok.Builder;

public record FollowingsResponse(
        List<FollowingUserInfo> followings
) {

    public static FollowingsResponse of(List<User> users) {
        return new FollowingsResponse(FollowingUserInfo.toList(users));
    }
}

@Builder
record FollowingUserInfo(
        Long id,
        String nickname,
        String profileImageUrl
) {

    public static List<FollowingUserInfo> toList(List<User> users) {
        return users.stream()
                .map(FollowingUserInfo::of)
                .toList();
    }

    public static FollowingUserInfo of(User user) {
        return FollowingUserInfo.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
