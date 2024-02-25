package com.listywave.user.application.service;

import static com.listywave.common.exception.ErrorCode.ALREADY_FOLLOWED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.ALREADY_NOT_FOLLOWED_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.alarm.repository.AlarmRepository;
import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.repository.CollaboratorRepository;
import com.listywave.collection.repository.CollectionRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.history.repository.HistoryRepository;
import com.listywave.image.application.service.ImageService;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.ItemRepository;
import com.listywave.list.repository.label.LabelRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.AllUserListsResponse;
import com.listywave.user.application.dto.FollowersResponse;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.RecommendUsersResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.dto.UserProflieUpdateCommand;
import com.listywave.user.application.dto.search.AllUserSearchResponse;
import com.listywave.user.application.dto.search.UserSearchResponse;
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
    private final CollectionRepository collectionRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final HistoryRepository historyRepository;
    private final ImageService imageService;
    private final AlarmRepository alarmRepository;
    private final ListRepository listRepository;
    private final ItemRepository itemRepository;
    private final LabelRepository labelRepository;
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
    public AllUserSearchResponse getUsersBySearch(Long loginUserId, String search, Pageable pageable) {
        if (loginUserId == null) {
            return getAllUserSearchResponse(null, search, pageable);
        }
        User user = userRepository.getById(loginUserId);
        return getAllUserSearchResponse(user.getId(), search, pageable);
    }

    private AllUserSearchResponse getAllUserSearchResponse(Long loginUserId, String search, Pageable pageable) {
        Long count = userRepository.getCollaboratorCount(search, loginUserId);
        Slice<UserSearchResponse> users = userRepository.getCollaborators(search, pageable, loginUserId);
        return AllUserSearchResponse.of(users.getContent(), count, users.hasNext());
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

    public FollowersResponse getFollowers(Long userId, Pageable pageable, String search, String cursorId) {
        User followingUser = userRepository.getById(userId);

        Slice<User> result =
                followRepository.findAllFollowerUserBy(followingUser, pageable, search, cursorId);
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
                .filter(followingUser -> !followingUser.getIsDelete())
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

    public void withdraw(Long userId) {
        User user = userRepository.getById(userId);
        user.validateUpdate(userId);
        user.softDelete();

        followRepository.getAllByFollowerUser(user).stream()
                .map(Follow::getFollowingUser) // 탈퇴하는 회원이 팔로우 하는 회원들 모두 조회해서
                .forEach(User::decreaseFollowerCount);

        followRepository.getAllByFollowingUser(user).stream()
                .map(Follow::getFollowerUser)
                .forEach(User::decreaseFollowingCount);
    }

    public void deleteLists(Long loginUserId, List<Long> listIds) {
        List<ListEntity> lists = listRepository.findAllById(listIds);
        if (lists.isEmpty()) {
            throw new CustomException(RESOURCE_NOT_FOUND);
        }
        User loginUser = userRepository.getById(loginUserId);
        validateOwner(lists, loginUser);

        deleteListImages(lists);
        alarmRepository.deleteAllByListIdIn(listIds);
        collectionRepository.deleteAllByListIn(lists);
        collaboratorRepository.deleteAllByListIn(lists);
        List<Comment> comments = commentRepository.findAllByListIn(lists);
        replyRepository.deleteAllByCommentIn(comments);
        commentRepository.deleteAllInBatch(comments);
        itemRepository.deleteAllListIn(lists);
        labelRepository.deleteAllListIn(lists);
        historyRepository.deleteAllByListIn(lists);
        listRepository.deleteAllInBatch(lists);
    }

    private void deleteListImages(List<ListEntity> lists) {
        lists.forEach(
                list -> {
                    imageService.deleteAllOfListImages(list.getId());
                }
        );
    }

    private void validateOwner(List<ListEntity> lists, User loginUser) {
        lists.forEach(
                list -> {
                    if (!list.canDeleteOrUpdateBy(loginUser)) {
                        throw new CustomException(INVALID_ACCESS, "리스트는 작성자만 삭제 가능합니다.");
                    }
                }
        );
    }
}
