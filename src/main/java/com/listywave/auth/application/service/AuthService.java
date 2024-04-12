package com.listywave.auth.application.service;

import static com.listywave.common.exception.ErrorCode.DELETED_USER_EXCEPTION;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.auth.application.domain.kakao.KakaoOauthClient;
import com.listywave.auth.application.domain.kakao.KakaoRedirectUriProvider;
import com.listywave.auth.application.dto.LoginResult;
import com.listywave.auth.application.dto.UpdateTokenResult;
import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.FollowRepository;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtManager jwtManager;
    private final UserRepository userRepository;
    private final ListRepository listRepository;
    private final KakaoOauthClient kakaoOauthClient;
    private final FollowRepository followRepository;
    private final KakaoRedirectUriProvider kakaoRedirectUriProvider;

    public String provideRedirectUri() {
        return kakaoRedirectUriProvider.provide();
    }

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
        if (user.isDelete()) {
            throw new CustomException(DELETED_USER_EXCEPTION);
        }
        user.updateKakaoAccessToken(kakaoAccessToken);
        String accessToken = jwtManager.createAccessToken(user.getId());
        String refreshToken = jwtManager.createRefreshToken(user.getId());
        return LoginResult.of(
                user,
                false,
                accessToken,
                refreshToken,
                jwtManager.getAccessTokenValidTimeDuration(),
                jwtManager.getRefreshTokenValidTimeDuration(),
                jwtManager.getAccessTokenValidTimeUnit(),
                jwtManager.getRefreshTokenValidTimeUnit()
        );
    }

    private LoginResult loginInit(Long kakaoId, String kakaoEmail, String kakaoAccessToken) {
        User user = User.init(kakaoId, kakaoEmail, kakaoAccessToken);
        User createdUser = userRepository.save(user);
        String accessToken = jwtManager.createAccessToken(user.getId());
        String refreshToken = jwtManager.createRefreshToken(user.getId());
        return LoginResult.of(
                createdUser,
                true,
                accessToken,
                refreshToken,
                jwtManager.getAccessTokenValidTimeDuration(),
                jwtManager.getRefreshTokenValidTimeDuration(),
                jwtManager.getAccessTokenValidTimeUnit(),
                jwtManager.getRefreshTokenValidTimeUnit()
        );
    }

    public void logout(Long userId) {
        User user = userRepository.getById(userId);
        user.validateHasKakaoAccessToken();
        kakaoOauthClient.logout(user.getKakaoAccessToken());
        user.updateKakaoAccessToken("");
    }

    public UpdateTokenResult updateToken(String refreshToken) {
//        Long userId = jwtManager.readRefreshToken(refreshToken);
        Long userId = jwtManager.readAccessToken(refreshToken);

        User user = userRepository.getById(userId);

        String accessToken = jwtManager.createAccessToken(user.getId());
        String newRefreshToken = jwtManager.createRefreshToken(user.getId());
        return new UpdateTokenResult(accessToken, newRefreshToken);
    }

    public void withdraw(Long userId) {
        User user = userRepository.getById(userId);
        user.validateUpdate(userId);
        user.softDelete();

        followRepository.getAllByFollowerUser(user).stream()
                .map(Follow::getFollowingUser)
                .forEach(User::decreaseFollowerCount);

        followRepository.getAllByFollowingUser(user).stream()
                .map(Follow::getFollowerUser)
                .forEach(User::decreaseFollowingCount);

        List<ListEntity> lists = listRepository.findAllCollectedListBy(userId);
        lists.forEach(ListEntity::decreaseCollectCount);

        kakaoOauthClient.logout(user.getKakaoAccessToken());
    }
}
