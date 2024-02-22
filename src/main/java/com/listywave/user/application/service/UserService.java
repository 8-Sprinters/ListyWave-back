package com.listywave.user.application.service;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.repository.CollaboratorRepository;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final CollaboratorRepository collaboratorRepository;
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
    public AllUserListsResponse getAllListOfUser(
            Long targetUserId,
            String type,
            CategoryType category,
            Long cursorId,
            Pageable pageable
    ) {
        userRepository.getById(targetUserId);

        List<Collaborator> collaborators = collaboratorRepository.findAllByUserId(targetUserId);
        Slice<ListEntity> result =
                userRepository.findFeedLists(collaborators, targetUserId, type, category, cursorId, pageable);
        List<ListEntity> lists = result.getContent();

        cursorId = null;
        if (!lists.isEmpty()) {
            cursorId = lists.get(lists.size() - 1).getId();
        }
        return AllUserListsResponse.of(result.hasNext(), cursorId, lists);
    }

    @Transactional(readOnly = true)
    public AllUserResponse getAllUser() {
        List<User> allUser = userRepository.findAll();
        return AllUserResponse.of(allUser);
    }

    public FollowingsResponse getFollowings(Long followerUserId, String search) {
        User followerUser = userRepository.getById(followerUserId);
        List<User> followingUsers = followRepository.findAllByFollowerUserOrderByFollowingUserNicknameAsc(followerUser, search);
        return FollowingsResponse.of(followingUsers);
    }

    public void follow(Long followingUserId, Long followerUserId) {
        User followingUser = userRepository.getById(followingUserId);
        User followerUser = userRepository.getById(followerUserId);

        Follow follow = new Follow(followingUser, followerUser);
        followRepository.save(follow);

        followerUser.follow(followingUser);

        applicationEventPublisher.publishEvent(AlarmEvent.follow(followerUser, followingUser));
    }

    public void unfollow(Long followingUserId, Long followerUserId) {
        User followingUser = userRepository.getById(followingUserId);
        User followerUser = userRepository.getById(followerUserId);

        followRepository.deleteByFollowingUserAndFollowerUser(followingUser, followerUser);

        followerUser.unfollow(followingUser);
    }

    public FollowersResponse getFollowers(Long userId, Pageable pageable, String search, String cursorId) {
        User followingUser = userRepository.getById(userId);

        Slice<User> result =
                followRepository.findAllByFollowingUserOrderByFollowerUserNicknameAsc(followingUser, pageable, search, cursorId);
        List<User> followerUserList = result.getContent();

        if (followerUserList.isEmpty()) {
            return FollowersResponse.empty();
        }
        int totalCount = followRepository.countByFollowingUser(followingUser);

        return FollowersResponse.of(followerUserList, totalCount, result.hasNext());
    }

    @Transactional(readOnly = true)
    public List<RecommendUsersResponse> getRecommendUsers(Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        List<Follow> follows = followRepository.getAllByFollowerUser(user);

        List<User> myFollowingUsers = follows.stream()
                .map(Follow::getFollowingUser)
                .toList();

        List<User> recommendUsers = userRepository.getRecommendUsers(myFollowingUsers, user);
        return recommendUsers.stream()
                .map(RecommendUsersResponse::of)
                .toList();
    }

    public void updateUserProfile(Long targetUserId, Long loginUserId, UserProflieUpdateCommand profile) {
        User targetUser = userRepository.getById(targetUserId);
        targetUser.validateUpdate(loginUserId);
        targetUser.updateUserProfile(
                profile.nickname(),
                profile.description(),
                profile.profileImageUrl(),
                profile.backgroundImageUrl()
        );
    }

    @Transactional(readOnly = true)
    public Boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNicknameValue(nickname);
    }

    public void deleteFollower(Long followerUserId, Long followingUserId) {
        User targetUser = userRepository.getById(followerUserId);
        User loginUser = userRepository.getById(followingUserId);

        followRepository.deleteByFollowingUserAndFollowerUser(loginUser, targetUser);
    }
}
