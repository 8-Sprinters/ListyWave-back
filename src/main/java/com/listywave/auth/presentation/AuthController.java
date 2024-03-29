package com.listywave.auth.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.auth.application.dto.LoginResult;
import com.listywave.auth.application.dto.UpdateTokenResult;
import com.listywave.auth.application.service.AuthService;
import com.listywave.auth.presentation.dto.LoginResponse;
import com.listywave.auth.presentation.dto.UpdateTokenResponse;
import com.listywave.common.auth.Auth;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    ResponseEntity<LoginResponse> login(
            @RequestParam("code") String authCode
    ) {
        LoginResult loginResult = authService.login(authCode);

//        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(loginResult.refreshToken());
        LoginResponse response = LoginResponse.of(loginResult);

        return ResponseEntity.ok()
//                .header(SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

//    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
//        return ResponseCookie.from("RefreshToken")
//                .value(refreshToken)
//                .maxAge(Duration.ofSeconds(REFRESH_TOKEN_VALID_SECOND))
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("None")
//                .build();
//    }

    @PatchMapping("/auth/kakao")
    ResponseEntity<Void> logout(@Auth Long loginUserId) {
        authService.logout(loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auth/token")
    ResponseEntity<UpdateTokenResponse> updateToken(
//            @CookieValue("RefreshToken") String refreshToken
            @RequestHeader(AUTHORIZATION) String refreshToken
    ) {
        UpdateTokenResult result = authService.updateToken(refreshToken);
//        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
//                .header(SET_COOKIE, refreshTokenCookie.toString())
                .body(new UpdateTokenResponse(result.accessToken()));
    }

    @DeleteMapping("/withdraw")
    ResponseEntity<Void> withdraw(@Auth Long userId) {
        authService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }
}
