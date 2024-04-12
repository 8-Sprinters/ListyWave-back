package com.listywave.common.auth;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.auth.application.domain.JwtManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenReader {

    private final JwtManager jwtManager;

    @Nullable
    public Long readAccessToken(HttpServletRequest request) {
        String authorizationValue = request.getHeader(AUTHORIZATION);
        if (authorizationValue != null && !authorizationValue.isBlank()) {
            return jwtManager.readTokenWithPrefix(authorizationValue);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        Optional<Cookie> cookieValue = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("accessToken") || cookie.getName().equals("refreshToken"))
                .findFirst();
        return cookieValue.map(cookie -> jwtManager.readTokenWithoutPrefix(cookie.getValue())).orElse(null);
    }
}
