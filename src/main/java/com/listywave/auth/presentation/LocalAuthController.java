package com.listywave.auth.presentation;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.auth.presentation.dto.LoginResponse;
import com.listywave.common.exception.CustomException;
import com.listywave.common.util.TimeUtils;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!prod")
@RequiredArgsConstructor
public class LocalAuthController {

    private final UserRepository userRepository;
    private final JwtManager jwtManager;

    @Value("${local-login.id}")
    private String id;
    @Value("${local-login.password}")
    private String password;

    @GetMapping("/login/local")
    ResponseEntity<LoginResponse> localLogin(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "password") String password
    ) {
        if (this.id.equals(id) && this.password.equals(password)) {
            User user = userRepository.getById(1L);

            String accessToken = jwtManager.createAccessToken(user.getId());
            String refreshToken = jwtManager.createRefreshToken(user.getId());

            ResponseCookie accessTokenCookie = createCookie(
                    "accessToken",
                    accessToken,
                    Duration.ofSeconds(TimeUtils.convertTimeUnit(
                            jwtManager.getAccessTokenValidTimeDuration(),
                            jwtManager.getAccessTokenValidTimeUnit(),
                            SECONDS
                    ))
            );
            ResponseCookie refreshTokenCookie = createCookie(
                    "refreshToken",
                    refreshToken,
                    Duration.ofSeconds(TimeUtils.convertTimeUnit(
                            jwtManager.getRefreshTokenValidTimeDuration(),
                            jwtManager.getRefreshTokenValidTimeUnit(),
                            SECONDS
                    ))
            );

            return ResponseEntity.ok()
                    .header(SET_COOKIE, accessTokenCookie.toString())
                    .header(SET_COOKIE, refreshTokenCookie.toString())
                    .body(LoginResponse.of(user, accessToken, refreshToken));
        }
        throw new CustomException(INVALID_ACCESS);
    }

    private ResponseCookie createCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name)
                .value(value)
                .maxAge(maxAge)
                .domain("dev.api.listywave.com")
                .path("/")
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .build();
    }
}
