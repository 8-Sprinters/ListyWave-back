package com.listywave.admin;

public record AdminLoginResponse(
        String accessToken,
        String refreshToken
) {
}
