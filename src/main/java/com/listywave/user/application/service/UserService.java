package com.listywave.user.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.common.util.UserUtil;
import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.Lists;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.AllUserListsResponse;
import com.listywave.user.application.dto.AllUserResponse;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.RecommendUsersResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUtil userUtil;
    private final JwtManager jwtManager;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId, String accessToken) {
        User user = userRepository.getById(userId);

        if (isSignedIn(accessToken)) {
            return UserInfoResponse.of(user, false, false);
        }

        Long loginUserId = jwtManager.read(accessToken);
        if (user.isSame(loginUserId)) {
            return UserInfoResponse.of(user, false, true);
        }
        return UserInfoResponse.of(user, false, false);
    }

    private boolean isSignedIn(String accessToken) {
        return accessToken.isBlank();
    }

    @Transactional(readOnly = true)
    public AllUserListsResponse getAllListOfUser(
            Long userId,
            String type,
            CategoryType category,
            Long cursorId,
            int size
    ) {
        userUtil.getUserByUserid(userId);

        List<Lists> feedList = userRepository.findFeedLists(userId, type, category, cursorId, size);

        boolean hasNext = false;
        cursorId = null;

        if (feedList.size() == size + 1) {
            feedList.remove(size);
            hasNext = true;
            cursorId = feedList.get(feedList.size() - 1).getId();
        }
        return AllUserListsResponse.of(hasNext, cursorId, feedList);
    }

    @Transactional(readOnly = true)
    public AllUserResponse getAllUser() {
        List<User> allUser = userRepository.findAll();
        return AllUserResponse.of(allUser);
    }

    public FollowingsResponse getFollowings(String accessToken) {
        Long loginUserId = jwtManager.read(accessToken);
        User user = userRepository.getById(loginUserId);
        List<Follow> follows = followRepository.getAllByFollowerUser(user);

        List<User> followingUsers = follows.stream()
                .map(Follow::getFollowingUser)
                .toList();
        return FollowingsResponse.of(followingUsers);
    }

    public void follow(Long followingUserId, String accessToken) {
        User followingUser = userRepository.getById(followingUserId);

        Long followerUserId = jwtManager.read(accessToken);
        User followerUser = userRepository.getById(followerUserId);

        Follow follow = new Follow(followingUser, followerUser);
        followRepository.save(follow);
    }

    public void unfollow(Long followingUserId, String accessToken) {
        User followingUser = userRepository.getById(followingUserId);

        Long loginUserId = jwtManager.read(accessToken);
        User followerUser = userRepository.getById(loginUserId);

        followRepository.deleteByFollowingUserAndFollowerUser(followingUser, followerUser);
    }
  
     @Transactional(readOnly = true)
     public List<RecommendUsersResponse> getRecommendUsers() {
         List<User> recommendUsers = userRepository.getRecommendUsers();
         return recommendUsers.stream()
                .map(RecommendUsersResponse::of)
                .toList();
     }
}
