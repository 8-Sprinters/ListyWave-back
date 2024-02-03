package com.listywave.user.presentation;

import com.listywave.list.application.domain.CategoryType;
import com.listywave.user.application.dto.AllUserResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.service.UserService;
import com.listywave.user.presentation.dto.response.AllUserListsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken
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
            @RequestParam(name = "size") int size
    ) {
        AllUserListsResponse allUserListsResponse = userService.getAllListOfUser(userId, type, category, cursorId, size);
        return ResponseEntity.ok(allUserListsResponse);
    }
}
