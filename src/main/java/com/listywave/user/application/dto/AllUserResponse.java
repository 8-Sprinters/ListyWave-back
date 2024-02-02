package com.listywave.user.application.dto;

import com.listywave.user.application.domain.User;
import java.util.List;
import lombok.Builder;

public record AllUserResponse(
        List<UserInfo> userInfos
) {

    public static AllUserResponse of(List<User> users) {
        List<UserInfo> userInfos = users.stream()
                .map(UserInfo::of)
                .toList();
        return new AllUserResponse(userInfos);
    }
}

@Builder
record UserInfo(
        Long id,
        String profileImageUrl,
        String nickname
) {

    public static UserInfo of(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .build();
    }
}
