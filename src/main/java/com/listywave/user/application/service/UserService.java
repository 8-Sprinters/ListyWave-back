package com.listywave.user.application.service;

import static com.listywave.common.exception.ErrorCode.NOT_FOUND;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.common.exception.CustomException;
import com.listywave.common.util.UserUtil;
import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.Lists;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.AllUserListsResponse;
import com.listywave.user.application.dto.AllUserResponse;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import java.util.Optional;
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
        Optional<User> found = userRepository.findById(userId);
        User foundUser = found.orElseThrow(() -> new CustomException(NOT_FOUND));

        if (isSignedIn(accessToken)) {
            return UserInfoResponse.of(foundUser, false, false);
        }

        Long loginUserId = jwtManager.read(accessToken);
        if (foundUser.isSame(loginUserId)) {
            return UserInfoResponse.of(foundUser, false, true);
        }
        return UserInfoResponse.of(foundUser, false, false);
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
}
