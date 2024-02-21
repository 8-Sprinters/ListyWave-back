package com.listywave.auth.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.auth.application.domain.kakao.KakaoOauthClient;
import com.listywave.auth.application.domain.kakao.KakaoRedirectUriProvider;
import com.listywave.auth.application.dto.LoginResult;
import com.listywave.auth.application.dto.UpdateTokenResult;
import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtManager jwtManager;
    private final UserRepository userRepository;
    private final KakaoOauthClient kakaoOauthClient;
    private final KakaoRedirectUriProvider kakaoRedirectUriProvider;

    public String provideRedirectUri() {
        return kakaoRedirectUriProvider.provide();
    }

    @Transactional
    public LoginResult login(String authCode) {
        KakaoTokenResponse kakaoTokenResponse = kakaoOauthClient.requestToken(authCode);
        KakaoMember kakaoMember = kakaoOauthClient.fetchMember(kakaoTokenResponse.accessToken());

        Optional<User> foundUser = userRepository.findByOauthId(kakaoMember.id());
        return foundUser.map(user -> loginNonInit(user, kakaoTokenResponse.accessToken()))
                .orElseGet(() -> loginInit(
                        kakaoMember.id(),
                        kakaoMember.kakaoAccount().email(),
                        kakaoTokenResponse.accessToken())
                );
    }

    private LoginResult loginNonInit(User user, String kakaoAccessToken) {
        user.updateKakaoAccessToken(kakaoAccessToken);
        String accessToken = jwtManager.createAccessToken(user.getId());
        String refreshToken = jwtManager.createRefreshToken(user.getId());
        return LoginResult.of(user, true, accessToken, refreshToken);
    }

    private LoginResult loginInit(Long kakaoId, String kakaoEmail, String kakaoAccessToken) {
        User user = User.init(kakaoId, kakaoEmail, kakaoAccessToken);
        User createdUser = userRepository.save(user);
        String accessToken = jwtManager.createAccessToken(user.getId());
        String refreshToken = jwtManager.createRefreshToken(user.getId());
        return LoginResult.of(createdUser, true, accessToken, refreshToken);
    }

    public void logout(Long userId) {
        User user = userRepository.getById(userId);
        kakaoOauthClient.logout(user.getKakaoAccessToken());
    }

    public UpdateTokenResult updateToken(String refreshToken) {
        Long userId = jwtManager.read(refreshToken);

        String accessToken = jwtManager.createAccessToken(userId);
        String newRefreshToken = jwtManager.createRefreshToken(userId);
        return new UpdateTokenResult(accessToken, newRefreshToken);
    }
}
