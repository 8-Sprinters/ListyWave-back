package com.listywave.user.repository.follow.custom;

import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import java.util.List;

public interface CustomFollowRepository {

    List<Follow> findAllByFollowingUser(User user, int size, int cursorId);
}
