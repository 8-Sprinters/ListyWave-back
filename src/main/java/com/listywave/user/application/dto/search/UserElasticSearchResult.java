package com.listywave.user.application.dto.search;

import com.listywave.user.application.domain.UserDocument;
import lombok.Builder;

@Builder
public record UserElasticSearchResult(
        Long id,
        String nickname,
        String profileImageUrl
) {

    public static UserElasticSearchResult of(UserDocument user) {
        return UserElasticSearchResult.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
