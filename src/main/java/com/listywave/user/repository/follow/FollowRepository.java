package com.listywave.user.repository.follow;

import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    List<Follow> getAllByFollowerUser(User user);

    void deleteByFollowingUserAndFollowerUser(User following, User follower);
}
