package com.listywave.user.repository.follow;

import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.custom.CustomFollowRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long>, CustomFollowRepository {

    List<Follow> getAllByFollowerUser(User followerUser);

    List<Follow> getAllByFollowingUser(User followingUser);

    void deleteByFollowingUserAndFollowerUser(User following, User follower);

    boolean existsByFollowerUserAndFollowingUser(User followerUser, User followingUser);
}
