package com.listywave.user.repository.user.custom;

import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.search.UserSearchResult;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomUserRepository {

    List<User> getRecommendUsers(List<User> myFollowingUsers, User user);

    Long countBySearch(String search, Long loginUserId);

    Slice<UserSearchResult> findAllBySearch(String search, Pageable pageable, Long loginUserId);
}
