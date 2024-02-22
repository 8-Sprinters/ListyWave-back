package com.listywave.user.repository.follow.custom;

import com.listywave.user.application.domain.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;

public interface CustomFollowRepository {

    @Query("""
            select u
            from Follow f
            join User u on f.followerUser.id = u.id
            where f.followerUser = :followerUser
                and u.nickname.value LIKE concat('%', :search, '%')
                and u.isDelete = false
            order by u.nickname.value asc
            """)
    Slice<User> findAllFollowerUserBy(User user, Pageable pageable, String search, String cursorId);

    @Query("""
            select u
            from Follow f
            join User u on f.followingUser.id = u.id
            where f.followerUser = :followerUser
                and u.nickname.value LIKE concat('%', :search, '%')
                and u.isDelete = false
            order by u.nickname.value asc
            """)
    List<User> findAllFollowingUserBy(User followerUser, String search);
}
