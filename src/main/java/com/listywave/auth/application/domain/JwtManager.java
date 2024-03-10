package com.listywave.auth.application.domain;

import static com.listywave.common.exception.ErrorCode.REQUIRED_ACCESS_TOKEN;
import static com.listywave.common.exception.ErrorCode.REQUIRED_REFRESH_TOKEN;
import static com.listywave.common.util.TimeUtils.convertTimeUnit;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.amazonaws.util.Base64;
import com.listywave.common.exception.CustomException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;

public class JwtManager {

    private static final String PREFIX = "Bearer ";

    private final SecretKey secretKey;
    private final String issuer;
    private final int accessTokenValidTimeDuration;
    private final int refreshTokenValidTimeDuration;
    private final TimeUnit accessTokenValidTimeUnit;
    private final TimeUnit refreshTokenValidTimeUnit;

    public JwtManager(
            String plainSecretKey,
            String issuer,
            int accessTokenValidTimeDuration,
            TimeUnit accessTokenValidTimeUnit,
            int refreshTokenValidTimeDuration,
            TimeUnit refreshTokenValidTimeUnit
    ) {
        byte[] encoded = Base64.encode(plainSecretKey.getBytes(UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(encoded);
        this.issuer = issuer;
        this.accessTokenValidTimeDuration = accessTokenValidTimeDuration;
        this.accessTokenValidTimeUnit = accessTokenValidTimeUnit;
        this.refreshTokenValidTimeDuration = refreshTokenValidTimeDuration;
        this.refreshTokenValidTimeUnit = refreshTokenValidTimeUnit;
    }

    public String createAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .header().type("jwt").and()
                .signWith(secretKey)
                .issuer(issuer)
                .issuedAt(now)
                .subject(String.valueOf(userId))
                .expiration(new Date(now.getTime() + convertTimeUnit(accessTokenValidTimeDuration, accessTokenValidTimeUnit, MILLISECONDS)))
                .issuedAt(Date.from(Instant.now()))
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .header().type("jwt").and()
                .signWith(secretKey)
                .issuer(issuer)
                .issuedAt(now)
                .subject(String.valueOf(userId))
                .expiration(new Date(now.getTime() + convertTimeUnit(refreshTokenValidTimeDuration, refreshTokenValidTimeUnit, MILLISECONDS)))
                .issuedAt(Date.from(Instant.now()))
                .compact();
    }

    public Long readAccessToken(String token) {
        if (token == null || token.isBlank() || !token.startsWith(PREFIX)) {
            throw new CustomException(REQUIRED_ACCESS_TOKEN);
        }
        token = token.substring(PREFIX.length());

        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }

    public Long readRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(REQUIRED_REFRESH_TOKEN);
        }

        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }
}
