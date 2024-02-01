package com.listywave.auth.service;

import com.listywave.auth.domain.JwtManager;
import com.listywave.auth.domain.kakao.KakaoOauthClient;
import com.listywave.auth.domain.kakao.KakaoRedirectUriProvider;
import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import com.listywave.auth.service.response.LoginResponse;
import com.listywave.user.domain.User;
import com.listywave.user.repository.UserRepository;
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

    @Transactional(readOnly = true)
    public LoginResponse login(String authCode) {
        KakaoTokenResponse kakaoTokenResponse = kakaoOauthClient.requestToken(authCode);
        KakaoMember kakaoMember = kakaoOauthClient.fetchMember(kakaoTokenResponse.accessToken());

        Optional<User> foundUser = userRepository.findByOauthId(kakaoMember.id());
        if (foundUser.isEmpty()) {
            User user = User.initialCreate(kakaoMember.id(), kakaoMember.kakaoAccount().email());
            User createdUser = userRepository.save(user);
            return LoginResponse.of(createdUser, true);
        }
        return LoginResponse.of(foundUser.get(), false);
    }

    public String createToken(Long userId) {
        return jwtManager.createToken(userId);
    }
}
