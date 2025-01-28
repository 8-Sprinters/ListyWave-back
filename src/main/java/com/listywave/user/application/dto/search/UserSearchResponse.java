package com.listywave.user.application.dto.search;

import java.util.List;
import java.util.Map;

public record UserSearchResponse(
        List<UserDto> users,
        Long totalCount,
        Boolean hasNext
) {

    public static UserSearchResponse createWithoutLogin(List<UserSearchResult> userSearchResults, Long totalCount, Boolean hasNext) {
        return new UserSearchResponse(
                userSearchResults.stream().map(UserDto::from).toList(),
                totalCount,
                hasNext
        );
    }

    public static UserSearchResponse createWithLogin(Map<UserSearchResult, Boolean> 팔로우_유무, Long totalCount, Boolean hasNext) {
        return new UserSearchResponse(
                팔로우_유무.entrySet().stream().map(UserDto::fromEntry).toList(),
                totalCount,
                hasNext
        );
    }

    public record UserDto(
            Long id,
            String nickname,
            String profileImageUrl,
            boolean isFollowing // 검색하는 자가 검색 대상인 유저를 팔로우하고 있는 지에 대한 여부입니다.
    ) {

        public static UserDto from(UserSearchResult userSearchResult) {
            return new UserDto(
                    userSearchResult.id,
                    userSearchResult.nickname,
                    userSearchResult.profileImageUrl,
                    false
            );
        }

        public static UserDto fromEntry(Map.Entry<UserSearchResult, Boolean> entry) {
            return new UserDto(
                    entry.getKey().id,
                    entry.getKey().nickname,
                    entry.getKey().profileImageUrl,
                    entry.getValue()
            );
        }
    }
}
