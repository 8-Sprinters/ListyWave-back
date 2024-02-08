package com.listywave.user.application.dto;

import com.listywave.user.application.domain.User;
import lombok.Builder;

@Builder
public record RecommendUsersResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {

    public static RecommendUsersResponse of(User user) {
        return RecommendUsersResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
