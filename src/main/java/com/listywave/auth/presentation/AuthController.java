package com.listywave.auth.presentation;

import com.listywave.auth.application.dto.LoginResponse;
import com.listywave.auth.application.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @GetMapping("/auth/redirect/kakao")
    ResponseEntity<LoginResponse> login(
            @RequestParam(name = "code") String authCode,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authService.login(authCode);

        String accessToken = authService.createToken(loginResponse.id());

        response.setHeader(HttpHeaders.SET_COOKIE, accessToken);
        return ResponseEntity.ok(loginResponse);
    }
}
