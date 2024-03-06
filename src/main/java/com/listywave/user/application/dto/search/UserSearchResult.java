package com.listywave.user.application.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSearchResult {
    Long id;
    String nickname;
    String profileImageUrl;
}
