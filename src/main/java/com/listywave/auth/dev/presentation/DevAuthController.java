package com.listywave.auth.dev.presentation;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.auth.application.dto.LoginResponse;
import com.listywave.auth.dev.domain.DevAccount;
import com.listywave.auth.dev.repository.DevAccountRepository;
import com.listywave.user.application.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("!prod")
@RequiredArgsConstructor
public class DevAuthController {

    private final JwtManager jwtManager;
    private final DevAccountRepository devAccountRepository;

    @PostMapping("/login/local")
    ResponseEntity<LoginResponse> localLogin(@RequestBody LocalLoginRequest request) {
        String account = request.account();
        String password = request.password();

        Optional<DevAccount> optional = devAccountRepository.findByAccount(account);
        DevAccount devAccount = optional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        devAccount.validatePassword(password);
        User user = devAccount.getUser();
        String accessToken = jwtManager.createAccessToken(user.getId());
        String refreshToken = jwtManager.createRefreshToken(user.getId());
        LoginResponse response = LoginResponse.of(user, accessToken, refreshToken, false);
        return ResponseEntity.ok(response);
    }
}
