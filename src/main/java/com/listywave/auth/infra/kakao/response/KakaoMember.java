package com.listywave.auth.infra.kakao.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public record KakaoMember(
        Long id,
        KakaoAccount kakaoAccount
) {

    @JsonNaming(SnakeCaseStrategy.class)
    public record KakaoAccount(
            boolean emailNeedsAgreement,
            boolean isEmailValid,
            boolean isEmailVerified,
            String email
    ) {
    }
}
