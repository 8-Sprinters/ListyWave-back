package com.listywave.user.repository.follow.custom;

import com.listywave.user.application.domain.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFollowRepository {

    Slice<User> findAllFollowerUserBy(User followingUser, Pageable pageable, String search, String cursorId);

    List<User> findAllFollowingUserBy(User followerUser, String search);
}
