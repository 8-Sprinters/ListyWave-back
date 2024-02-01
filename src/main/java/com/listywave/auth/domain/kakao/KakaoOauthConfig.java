package com.listywave.auth.domain.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoOauthConfig(
        String clientId,
        String clientSecret,
        String redirectUri
) {
}
