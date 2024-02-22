package com.listywave.user.application.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSearchResponse {
    Long id;
    String nickname;
    String profileImageUrl;
}
