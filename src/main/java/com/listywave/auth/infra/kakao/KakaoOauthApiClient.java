package com.listywave.auth.infra.kakao;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

public interface KakaoOauthApiClient {

    @PostExchange(
            url = "https://kauth.kakao.com/oauth/token",
            contentType = "application/x-www-form-urlencoded;charset=utf-8"
    )
    KakaoTokenResponse requestToken(@RequestBody MultiValueMap<String, String> parameters);

    @PostExchange(
            url = "https://kapi.kakao.com/v2/user/me",
            contentType = "application/x-www-form-urlencoded;charset=utf-8"
    )
    KakaoMember fetchKakaoMember(@RequestHeader(name = AUTHORIZATION) String accessToken);
}
