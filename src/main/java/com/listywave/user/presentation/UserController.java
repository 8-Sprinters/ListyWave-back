package com.listywave.user.presentation;

import com.listywave.common.auth.Auth;
import com.listywave.common.auth.OptionalAuth;
import com.listywave.user.application.dto.FollowersResponse;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.RecommendUsersResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.dto.search.UserElasticSearchResponse;
import com.listywave.user.application.dto.search.UserSearchResponse;
import com.listywave.user.application.service.UserService;
import com.listywave.user.presentation.dto.ListVisibilityUpdateRequest;
import com.listywave.user.presentation.dto.UserProfileUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    ResponseEntity<UserInfoResponse> getUserInfo(
            @PathVariable(value = "userId") Long userId,
            @OptionalAuth Long loginUserId
    ) {
        UserInfoResponse response = userService.getUserInfo(userId, loginUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    ResponseEntity<UserSearchResponse> searchUser(
            @OptionalAuth Long loginUserId,
            @RequestParam(name = "search", defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        UserSearchResponse response = userService.searchUser(loginUserId, search, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/followings")
    ResponseEntity<FollowingsResponse> getFollowings(
            @PathVariable("userId") Long followerUserId,
            @RequestParam(name = "search", defaultValue = "") String search
    ) {
        FollowingsResponse response = userService.getFollowings(followerUserId, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/followers")
    ResponseEntity<FollowersResponse> getFollowers(
            @PathVariable("userId") Long followingUserId,
            @RequestParam(name = "cursorId", required = false) String cursorId,
            @RequestParam(name = "search", defaultValue = "") String search,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FollowersResponse response = userService.getFollowers(followingUserId, pageable, search, cursorId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/follow/{userId}")
    ResponseEntity<Void> follow(
            @PathVariable("userId") Long followingUserId,
            @Auth Long followerUserId
    ) {
        userService.follow(followingUserId, followerUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/follow/{userId}")
    ResponseEntity<Void> unfollow(
            @PathVariable("userId") Long followingUserId,
            @Auth Long followerUserId
    ) {
        userService.unfollow(followingUserId, followerUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/recommend")
    ResponseEntity<List<RecommendUsersResponse>> getRecommendUsers(
            @Auth Long loginUserId
    ) {
        List<RecommendUsersResponse> recommendUsers = userService.getRecommendUsers(loginUserId);
        return ResponseEntity.ok(recommendUsers);
    }

    @PatchMapping("/users/{userId}")
    ResponseEntity<Void> updateUserProfile(
            @PathVariable("userId") Long targetUserId,
            @Auth Long loginUserId,
            @RequestBody UserProfileUpdateRequest request
    ) {
        userService.updateUserProfile(targetUserId, loginUserId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/exists")
    ResponseEntity<Boolean> checkNicknameDuplicate(
            @RequestParam("nickname") String nickname
    ) {
        return ResponseEntity.ok(userService.isDuplicateNickname(nickname));
    }

    @DeleteMapping("/followers/{userId}")
    ResponseEntity<Void> deleteFollower(
            @PathVariable("userId") Long followerUserId,
            @Auth Long followingUserId
    ) {
        userService.deleteFollower(followerUserId, followingUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/list-visibility")
    ResponseEntity<Void> updateListVisibility(
            @Auth Long loginUserId,
            @RequestBody ListVisibilityUpdateRequest request
    ) {
        userService.updateListVisibility(loginUserId, request.listId(), request.isPublic());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/v2/users")
    ResponseEntity<UserElasticSearchResponse> searchUserByElastic(
            @OptionalAuth Long loginUserId,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        UserElasticSearchResponse response = userService.searchUserByElastic(loginUserId, keyword, pageable);
        return ResponseEntity.ok(response);
    }
}
