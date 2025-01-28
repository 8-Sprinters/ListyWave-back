package com.listywave.user.repository.follow;

import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.custom.CustomFollowRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long>, CustomFollowRepository {

    List<Follow> getAllByFollowerUser(User 팔로우_하는_유저);

    List<Follow> getAllByFollowingUser(User 팔로우_당하는_유저);

    void deleteByFollowingUserAndFollowerUser(User 팔로우_하는_유저, User 팔로우_당하는_유저);

    boolean existsByFollowerUserAndFollowingUser(User 팔로우_하는_유저, User 팔로우_당하는_유저);

    @Query("""
            select f
            from Follow f
            where f.followerUser = :검색하는_유저 and f.followingUser.isDelete = false and f.followingUser.id in :검색_대상_유저_ID_리스트
            """)
    List<Follow> 검색하는_유저가_검색_결과_유저_중_팔로우하고_있는_유저만을_조회한다(User 검색하는_유저, List<Long> 검색_대상_유저_ID_리스트);
}
