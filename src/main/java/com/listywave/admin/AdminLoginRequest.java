package com.listywave.admin;

public record AdminLoginRequest(
        String account,
        String password
) {
}
