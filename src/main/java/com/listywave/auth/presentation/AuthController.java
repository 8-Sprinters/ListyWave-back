package com.listywave.auth.presentation;

import com.listywave.auth.application.dto.LoginResponse;
import com.listywave.auth.application.dto.UpdateTokenResponse;
import com.listywave.auth.application.service.AuthService;
import com.listywave.common.auth.Auth;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/auth/kakao")
    ResponseEntity<Void> redirectAuthCodeRequestUrl(HttpServletResponse response) throws IOException {
        String requestUrl = authService.provideRedirectUri();
        response.sendRedirect(requestUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/redirect/kakao")
    ResponseEntity<LoginResponse> login(@RequestParam("code") String authCode) {
        LoginResponse response = authService.login(authCode);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/auth/kakao")
    ResponseEntity<Void> logout(@Auth Long loginUserId) {
        authService.logout(loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auth/token")
    ResponseEntity<UpdateTokenResponse> updateToken(@Auth Long userId) {
        UpdateTokenResponse response = authService.updateToken(userId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/withdraw")
    ResponseEntity<Void> withdraw(@Auth Long userId) {
        authService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }
}
