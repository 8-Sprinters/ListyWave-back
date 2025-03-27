package com.listywave.auth.dev.presentation;

public record LocalLoginRequest(
        String account,
        String password
) {
}
