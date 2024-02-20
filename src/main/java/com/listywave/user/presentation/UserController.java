package com.listywave.user.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.user.application.dto.AllUserListsResponse;
import com.listywave.user.application.dto.AllUserResponse;
import com.listywave.user.application.dto.FollowersResponse;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.RecommendUsersResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.service.UserService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    ResponseEntity<UserInfoResponse> getUserInfo(
            @PathVariable(value = "userId") Long userId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        UserInfoResponse userInfoResponse = userService.getUserInfo(userId, accessToken);
        return ResponseEntity.ok(userInfoResponse);
    }

    @GetMapping("/users")
    ResponseEntity<AllUserResponse> getAllUser() {
        AllUserResponse allUserResponse = userService.getAllUser();
        return ResponseEntity.ok(allUserResponse);
    }

    @GetMapping("/users/{userId}/lists")
    ResponseEntity<AllUserListsResponse> getAllUserLists(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "type", defaultValue = "my") String type,
            @RequestParam(name = "category", defaultValue = "entire") CategoryType category,
            @RequestParam(name = "cursorId", required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        AllUserListsResponse allUserListsResponse =
                userService.getAllListOfUser(userId, type, category, cursorId, pageable);
        return ResponseEntity.ok(allUserListsResponse);
    }

    @GetMapping("/users/{userId}/followings")
    ResponseEntity<FollowingsResponse> getFollowings(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "search", defaultValue = "") String search
    ) {
        FollowingsResponse response = userService.getFollowings(userId, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/followers")
    ResponseEntity<FollowersResponse> getFollowers(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "cursorId", required = false) String cursorId,
            @RequestParam(name = "search", defaultValue = "") String search,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        FollowersResponse response = userService.getFollowers(userId, pageable, search, cursorId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/follow/{userId}")
    ResponseEntity<Void> follow(
            @PathVariable(value = "userId") Long followingUserId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        userService.follow(followingUserId, accessToken);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/follow/{userId}")
    ResponseEntity<Void> unfollow(
            @PathVariable(value = "userId") Long followingUserId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        userService.unfollow(followingUserId, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/recommend")
    ResponseEntity<List<RecommendUsersResponse>> getRecommendUsers() {
        List<RecommendUsersResponse> recommendUsers = userService.getRecommendUsers();
        return ResponseEntity.ok(recommendUsers);
    }


    @PatchMapping("/users/{userId}")
    ResponseEntity<Void> updateUserProfile(
            @PathVariable(value = "userId") Long userId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken,
            @RequestBody UserProfileUpdateRequest request
    ) {
        userService.updateUserProfile(userId, accessToken, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/exists")
    ResponseEntity<Boolean> checkNicknameDuplicate(
            @RequestParam(name = "nickname") String nickname
    ) {
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    @DeleteMapping("/followers/{userId}")
    ResponseEntity<Void> deleteFollower(
            @PathVariable("userId") Long targetUserId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        userService.deleteFollower(targetUserId, accessToken);
        return ResponseEntity.noContent().build();
    }
}
