package com.listywave.auth.application.dto;

import java.util.concurrent.TimeUnit;

public record UpdateTokenResult(
        String accessToken,
        String refreshToken,
        int accessTokenValidTimeDuration,
        int refreshTokenValidTimeDuration,
        TimeUnit accessTokenValidTimeUnit,
        TimeUnit refreshTokenValidTimeUnit
) {
}
