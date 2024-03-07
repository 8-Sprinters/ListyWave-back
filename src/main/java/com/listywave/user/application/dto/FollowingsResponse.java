package com.listywave.user.application.dto;

import com.listywave.user.application.domain.User;
import java.util.List;
import lombok.Builder;

public record FollowingsResponse(
        List<FollowingInfo> followings
) {

    public static FollowingsResponse of(List<User> users) {
        return new FollowingsResponse(FollowingInfo.toList(users));
    }

    @Builder
    public record FollowingInfo(
            Long id,
            String nickname,
            String profileImageUrl
    ) {

        public static List<FollowingInfo> toList(List<User> users) {
            return users.stream()
                    .map(FollowingInfo::of)
                    .toList();
        }

        public static FollowingInfo of(User user) {
            return FollowingInfo.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();
        }
    }
}
