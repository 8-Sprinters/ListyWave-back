package com.listywave.user.repository.follow.custom;

import com.listywave.user.application.domain.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFollowRepository {

    Slice<User> findAllFollowerUserBy(User followingUser, Pageable pageable, String search, String cursorNickname);

    Long countFollowerUserBy(User followingUser, String search, String cursorNickname);

    List<User> findAllFollowingUserBy(User followerUser, String search);
}
