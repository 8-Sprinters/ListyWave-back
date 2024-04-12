package com.listywave.auth.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.listywave.auth.application.dto.LoginResult;
import com.listywave.auth.application.dto.UpdateTokenResult;
import com.listywave.auth.application.service.AuthService;
import com.listywave.auth.presentation.dto.LoginResponse;
import com.listywave.auth.presentation.dto.UpdateTokenResponse;
import com.listywave.common.auth.Auth;
import com.listywave.common.util.TimeUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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

        ResponseCookie accessTokenCookie = createCookie(
                "accessToken",
                loginResult.accessToken(),
                Duration.ofSeconds(
                        TimeUtils.convertTimeUnit(loginResult.accessTokenValidTimeDuration(),
                                loginResult.accessTokenValidTimeUnit(),
                                TimeUnit.SECONDS)
                ),
                true,
                true,
                "sameSite"
        );
        ResponseCookie refreshTokenCookie = createCookie(
                "refreshToken",
                loginResult.refreshToken(),
                Duration.ofSeconds(
                        TimeUtils.convertTimeUnit(loginResult.refreshTokenValidTimeDuration(),
                                loginResult.refreshTokenValidTimeUnit(),
                                TimeUnit.SECONDS)
                ),
                true,
                true,
                "sameSite"
        );
        LoginResponse response = LoginResponse.of(loginResult);

        return ResponseEntity.ok()
                .header(SET_COOKIE, accessTokenCookie.toString())
                .header(SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    private ResponseCookie createCookie(String name, String value, Duration maxAge, boolean httpOnly, boolean secure, String sameSite) {
        return ResponseCookie.from(name)
                .value(value)
                .maxAge(maxAge)
                .httpOnly(httpOnly)
                .secure(secure)
                .sameSite(sameSite)
                .build();
    }

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
