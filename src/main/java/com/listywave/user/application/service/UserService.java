package com.listywave.user.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.repository.CollaboratorRepository;
import com.listywave.common.util.UserUtil;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.AllUserListsResponse;
import com.listywave.user.application.dto.AllUserResponse;
import com.listywave.user.application.dto.FollowersResponse;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.RecommendUsersResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.dto.UserProflieUpdateCommand;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserUtil userUtil;
    private final JwtManager jwtManager;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final CollaboratorRepository collaboratorRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId, String accessToken) {
        User user = userRepository.getById(userId);

        if (isGuest(accessToken)) {
            return UserInfoResponse.of(user, false, false);
        }

        Long loginUserId = jwtManager.read(accessToken);
        if (user.isSame(loginUserId)) {
            return UserInfoResponse.of(user, false, true);
        }
        User loginUser = userRepository.getById(loginUserId);
        boolean isFollowed = followRepository.existsByFollowerUserAndFollowingUser(loginUser, user);
        return UserInfoResponse.of(user, isFollowed, false);
    }

    private boolean isGuest(String accessToken) {
        return accessToken.isBlank();
    }

    @Transactional(readOnly = true)
    public AllUserListsResponse getAllListOfUser(
            Long userId,
            String type,
            CategoryType category,
            Long cursorId,
            Pageable pageable
    ) {
        userUtil.getUserByUserid(userId);

        List<Collaborator> collaboList = collaboratorRepository.findAllByUserId(userId);
        Slice<ListEntity> result =
                userRepository.findFeedLists(collaboList, userId, type, category, cursorId, pageable);
        List<ListEntity> feedList = result.getContent();

        cursorId = null;
        if (!feedList.isEmpty()) {
            cursorId = feedList.get(feedList.size() - 1).getId();
        }
        return AllUserListsResponse.of(result.hasNext(), cursorId, feedList);
    }

    @Transactional(readOnly = true)
    public AllUserResponse getAllUser() {
        List<User> allUser = userRepository.findAll();
        return AllUserResponse.of(allUser);
    }

    public FollowingsResponse getFollowings(Long userId) {
        User user = userRepository.getById(userId);
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

        followerUser.follow(followingUser);
    }

    public void unfollow(Long followingUserId, String accessToken) {
        User followingUser = userRepository.getById(followingUserId);

        Long loginUserId = jwtManager.read(accessToken);
        User followerUser = userRepository.getById(loginUserId);

        followRepository.deleteByFollowingUserAndFollowerUser(followingUser, followerUser);

        followerUser.unfollow(followingUser);
    }

    public FollowersResponse getFollowers(Long userId, int size, int cursorId) {
        User followingUser = userRepository.getById(userId);

        List<Follow> follows = followRepository.findAllByFollowingUser(followingUser, size, cursorId);
        List<User> followerUsers = follows.stream()
                .map(Follow::getFollowerUser)
                .toList();

        if (followerUsers.isEmpty()) {
            return FollowersResponse.empty();
        }

        int totalCount = followRepository.countByFollowingUser(followingUser);
        if (followerUsers.size() > size) {
            return FollowersResponse.of(followerUsers.subList(0, size), totalCount, true);
        }
        return FollowersResponse.of(followerUsers, totalCount, false);
    }

    @Transactional(readOnly = true)
    public List<RecommendUsersResponse> getRecommendUsers() {
        List<User> recommendUsers = userRepository.getRecommendUsers();
        return recommendUsers.stream()
                .map(RecommendUsersResponse::of)
                .toList();
    }

    public void updateUserProfile(Long userId, String accessToken, UserProflieUpdateCommand profile) {
        jwtManager.read(accessToken);
        User user = userRepository.getById(userId);
        user.updateUserProfile(
                profile.nickname(),
                profile.description(),
                profile.profileImageUrl(),
                profile.backgroundImageUrl()
        );
    }

    @Transactional(readOnly = true)
    public Boolean checkNicknameDuplicate(String nickname, String accessToken) {
        Long loginUserId = jwtManager.read(accessToken);
        userRepository.getById(loginUserId);
        return userRepository.existsByNicknameValue(nickname);
    }
}
