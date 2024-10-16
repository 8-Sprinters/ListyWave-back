package com.listywave.auth.application.domain.kakao;

import com.listywave.auth.infra.kakao.KakaoOauthApiClient;
import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class KakaoOauthClient {

    private static final String TOKEN_PREFIX = "Bearer ";
    private final KakaoOauthConfig kakaoOauthConfig;
    private final KakaoOauthApiClient apiClient;

    public KakaoTokenResponse requestToken(String authCode) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>() {{
            add("grant_type", "authorization_code");
            add("client_id", kakaoOauthConfig.clientId());
            add("redirect_uri", kakaoOauthConfig.redirectUri());
            add("code", authCode);
            add("client_secret", kakaoOauthConfig.clientSecret());
        }};

        return apiClient.requestToken(parameters);
    }

    public KakaoMember fetchMember(String accessToken) {
        return apiClient.fetchKakaoMember(TOKEN_PREFIX + accessToken);
    }

    public void logout(String oauthAccessToken) {
        String accessToken = TOKEN_PREFIX + oauthAccessToken;
        apiClient.logout(accessToken);
    }
}
