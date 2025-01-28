package com.listywave.user.application.service;

import static com.listywave.common.exception.ErrorCode.ALREADY_FOLLOWED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.ALREADY_NOT_FOLLOWED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.DUPLICATE_NICKNAME_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;

import com.listywave.alarm.application.domain.AlarmCreateEvent;
import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.FollowersResponse;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.dto.UserProflieUpdateCommand;
import com.listywave.user.application.dto.UsersRecommendedResponse;
import com.listywave.user.application.dto.search.UserElasticSearchResponse;
import com.listywave.user.application.dto.search.UserSearchResponse;
import com.listywave.user.application.dto.search.UserSearchResult;
import com.listywave.user.application.vo.Nickname;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import com.listywave.user.repository.user.elastic.UserElasticRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserElasticRepository userElasticRepository;
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
        Slice<UserSearchResult> searchResult = userRepository.findAllBySearch(search, pageable, loginUserId);
        Long count = userRepository.countBySearch(search, loginUserId);

        if (loginUserId == null) {
            return UserSearchResponse.createWithoutLogin(searchResult.getContent(), count, searchResult.hasNext());
        }

        User 검색하는_유저 = userRepository.getById(loginUserId);
        List<Long> 검색_결과_유저_ID_리스트 = searchResult.getContent().stream()
                .map(UserSearchResult::getId)
                .toList();
        List<Long> 검색하는_유저가_팔로우하고_있는_검색_결과_유저_ID_리스트 = followRepository.검색하는_유저가_검색_결과_유저_중_팔로우하고_있는_유저만을_조회한다(검색하는_유저, 검색_결과_유저_ID_리스트).stream()
                .map(Follow::getFollowingUser)
                .map(User::getId)
                .toList();

        Map<UserSearchResult, Boolean> 회원_검색_결과와_팔로우_여부 = searchResult.getContent().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        userSearchResult -> 검색하는_유저가_팔로우하고_있는_검색_결과_유저_ID_리스트.contains(userSearchResult.getId())
                ));

        return UserSearchResponse.createWithLogin(회원_검색_결과와_팔로우_여부, count, searchResult.hasNext());
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

        applicationEventPublisher.publishEvent(AlarmCreateEvent.follow(followingUser, followerUser));
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
    public List<UsersRecommendedResponse> getRecommendedUsers(Long loginUserId) {
        if (loginUserId == null) {
            List<User> recommendUsers = userRepository.getRecommendUsers(List.of(), null);
            return toUsersRecommendedResponse(recommendUsers);
        }
        User user = userRepository.getById(loginUserId);
        List<Follow> follows = followRepository.getAllByFollowerUser(user);
        List<User> myFollowingUsers = follows.stream()
                .map(Follow::getFollowingUser)
                .filter(followingUser -> !followingUser.isDelete())
                .toList();
        List<User> recommendUsers = userRepository.getRecommendUsers(myFollowingUsers, user);
        return toUsersRecommendedResponse(recommendUsers);
    }

    private List<UsersRecommendedResponse> toUsersRecommendedResponse(List<User> recommendUsers) {
        return recommendUsers.stream()
                .map(UsersRecommendedResponse::of)
                .toList();
    }

    public void updateUserProfile(Long targetUserId, Long loginUserId, UserProflieUpdateCommand command) {
        User targetUser = userRepository.getById(targetUserId);

        targetUser.validateUpdate(loginUserId);
        String newNickname;
        if (targetUser.getNickname().equals(command.nickname())) {
            newNickname = null;
        } else {
            newNickname = command.nickname();
            if (isDuplicateNickname(newNickname)) {
                throw new CustomException(DUPLICATE_NICKNAME_EXCEPTION);
            }
        }

        targetUser.updateUserProfile(
                newNickname,
                command.description(),
                command.profileImageUrl(),
                command.backgroundImageUrl()
        );
    }

    @Transactional(readOnly = true)
    public Boolean isDuplicateNickname(String nickname) {
        return userRepository.existsByNicknameValueIgnoreCase(nickname);
    }

    public void deleteFollower(Long 팔로우_하는_유저_ID, Long 팔로우_당하는_유저_ID) {
        User 팔로우_하는_유저 = userRepository.getById(팔로우_하는_유저_ID);
        User 팔로우_당하는_유저 = userRepository.getById(팔로우_당하는_유저_ID);

        followRepository.deleteByFollowingUserAndFollowerUser(팔로우_당하는_유저, 팔로우_하는_유저);

        팔로우_당하는_유저.removeFollower(팔로우_하는_유저);
    }

    @Transactional(readOnly = true)
    public UserElasticSearchResponse searchUserByElastic(@Nullable Long loginUserId, String keyword, Pageable pageable) {
        if (loginUserId == null) {
            return userElasticRepository.findAll(-1L, keyword, pageable);
        }
        User user = userRepository.getById(loginUserId);
        return userElasticRepository.findAll(user.getId(), keyword, pageable);
    }

    public User getById(Long userId) {
        return userRepository.getById(userId);
    }

    public void validateNickname(String nickname) {
        Nickname.of(nickname);
    }
}
