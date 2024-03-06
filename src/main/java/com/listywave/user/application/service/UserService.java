package com.listywave.user.application.service;

import static com.listywave.common.exception.ErrorCode.ALREADY_FOLLOWED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.ALREADY_NOT_FOLLOWED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.DUPLICATE_NICKNAME_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.FollowersResponse;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.RecommendUsersResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.dto.UserProflieUpdateCommand;
import com.listywave.user.application.dto.search.UserSearchResponse;
import com.listywave.user.application.dto.search.UserSearchResult;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long targetUserId, Long loginUserId) {
        User targetUser = userRepository.getById(targetUserId);

        if (loginUserId == null) {
            return UserInfoResponse.of(targetUser, false, false);
        }

        if (targetUser.isSame(loginUserId)) {
            return UserInfoResponse.of(targetUser, false, true);
        }
        User loginUser = userRepository.getById(loginUserId);
        boolean isFollowed = followRepository.existsByFollowerUserAndFollowingUser(loginUser, targetUser);
        return UserInfoResponse.of(targetUser, isFollowed, false);
    }

    @Transactional(readOnly = true)
    public UserSearchResponse searchUser(Long loginUserId, String search, Pageable pageable) {
        if (loginUserId == null) {
            return createUserSearchResponse(null, search, pageable);
        }
        User user = userRepository.getById(loginUserId);
        return createUserSearchResponse(user.getId(), search, pageable);
    }

    private UserSearchResponse createUserSearchResponse(Long loginUserId, String search, Pageable pageable) {
        Long count = userRepository.countBySearch(search, loginUserId);
        Slice<UserSearchResult> result = userRepository.findAllBySearch(search, pageable, loginUserId);
        return UserSearchResponse.of(result.getContent(), count, result.hasNext());
    }

    public FollowingsResponse getFollowings(Long followerUserId, String search) {
        User followerUser = userRepository.getById(followerUserId);
        List<User> followingUsers = followRepository.findAllFollowingUserBy(followerUser, search);
        return FollowingsResponse.of(followingUsers);
    }

    public void follow(Long followingUserId, Long followerUserId) {
        if (followingUserId.equals(followerUserId)) {
            throw new CustomException(INVALID_ACCESS, "본인을 팔로우 할 수 없습니다.");
        }

        User followingUser = userRepository.getById(followingUserId);
        User followerUser = userRepository.getById(followerUserId);

        if (followRepository.existsByFollowerUserAndFollowingUser(followerUser, followingUser)) {
            throw new CustomException(ALREADY_FOLLOWED_EXCEPTION);
        }

        followRepository.save(new Follow(followingUser, followerUser));
        followerUser.follow(followingUser);
        applicationEventPublisher.publishEvent(AlarmEvent.follow(followerUser, followingUser));
    }

    public void unfollow(Long followingUserId, Long followerUserId) {
        User followingUser = userRepository.getById(followingUserId);
        User followerUser = userRepository.getById(followerUserId);

        if (!followRepository.existsByFollowerUserAndFollowingUser(followerUser, followingUser)) {
            throw new CustomException(ALREADY_NOT_FOLLOWED_EXCEPTION);
        }

        followRepository.deleteByFollowingUserAndFollowerUser(followingUser, followerUser);
        followerUser.unfollow(followingUser);
    }

    public FollowersResponse getFollowers(Long userId, Pageable pageable, String search, String cursorNickname) {
        User followingUser = userRepository.getById(userId);

        Slice<User> result =
                followRepository.findAllFollowerUserBy(followingUser, pageable, search, cursorNickname);
        List<User> followerUserList = result.getContent();

        if (followerUserList.isEmpty()) {
            return FollowersResponse.empty();
        }
        Long totalCount = followRepository.countFollowerUserBy(followingUser, search, cursorNickname);

        return FollowersResponse.of(followerUserList, totalCount, result.hasNext());
    }

    @Transactional(readOnly = true)
    public List<RecommendUsersResponse> getRecommendUsers(Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        List<Follow> follows = followRepository.getAllByFollowerUser(user);

        List<User> myFollowingUsers = follows.stream()
                .map(Follow::getFollowingUser)
                .filter(followingUser -> !followingUser.getIsDelete())
                .toList();

        List<User> recommendUsers = userRepository.getRecommendUsers(myFollowingUsers, user);
        return recommendUsers.stream()
                .map(RecommendUsersResponse::of)
                .toList();
    }

    public void updateUserProfile(Long targetUserId, Long loginUserId, UserProflieUpdateCommand command) {
        User targetUser = userRepository.getById(targetUserId);

        targetUser.validateUpdate(loginUserId);
        if (isDuplicateNickname(command.nickname())) {
            throw new CustomException(DUPLICATE_NICKNAME_EXCEPTION);
        }

        targetUser.updateUserProfile(
                command.nickname(),
                command.description(),
                command.profileImageUrl(),
                command.backgroundImageUrl()
        );
    }

    @Transactional(readOnly = true)
    public Boolean isDuplicateNickname(String nickname) {
        return userRepository.existsByNicknameValue(nickname);
    }

    public void deleteFollower(Long followerUserId, Long followingUserId) {
        User followerUser = userRepository.getById(followerUserId);
        User followingUser = userRepository.getById(followingUserId);

        followRepository.deleteByFollowingUserAndFollowerUser(followingUser, followerUser);

        followingUser.remove(followerUser);
    }

    public void updateListVisibility(Long loginUserId, Long listId, Boolean beforeIsPublic) {
        User user = userRepository.getById(loginUserId);
        ListEntity list = listRepository.getById(listId);
        list.validateOwner(user);

        list.updateVisibility(!beforeIsPublic);
    }
}
