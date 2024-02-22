package com.listywave.auth.application.dto;

public record UpdateTokenResult(
        String accessToken,
        String refreshToken
) {
}
