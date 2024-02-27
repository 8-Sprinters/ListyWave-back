package com.listywave.user.application.dto;

import com.listywave.user.application.domain.User;
import java.util.Collections;
import java.util.List;
import lombok.Builder;

@Builder
public record FollowersResponse(
        List<FollowerInfo> followers,
        Long totalCount,
        String cursorId,
        boolean hasNext
) {

    public static FollowersResponse of(List<User> users, Long totalCount, boolean hasNext) {
        return FollowersResponse.builder()
                .followers(FollowerInfo.toList(users))
                .totalCount(totalCount)
                .cursorId(users.get(users.size() - 1).getNickname())
                .hasNext(hasNext)
                .build();
    }

    public static FollowersResponse empty() {
        return FollowersResponse.builder()
                .followers(Collections.emptyList())
                .totalCount(0L)
                .hasNext(false)
                .build();
    }
}

@Builder
record FollowerInfo(
        Long id,
        String nickname,
        String profileImageUrl
) {

    public static List<FollowerInfo> toList(List<User> users) {
        return users.stream()
                .map(FollowerInfo::of)
                .toList();
    }

    public static FollowerInfo of(User user) {
        return FollowerInfo.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
