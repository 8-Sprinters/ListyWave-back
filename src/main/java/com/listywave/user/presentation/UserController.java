package com.listywave.user.presentation;

import com.listywave.user.application.dto.AllUserResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
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
}
