package com.listywave.user.application.dto;

import com.listywave.user.application.domain.User;
import lombok.Builder;

@Builder
public record UsersRecommendedResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {

    public static UsersRecommendedResponse of(User user) {
        return UsersRecommendedResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
