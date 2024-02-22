package com.listywave.auth.application.domain;

import static com.listywave.common.exception.ErrorCode.REQUIRED_ACCESS_TOKEN;
import static com.listywave.common.exception.ErrorCode.REQUIRED_REFRESH_TOKEN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;

import com.amazonaws.util.Base64;
import com.listywave.common.exception.CustomException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtManager {

    private static final String PREFIX = "Bearer ";
    private static final String ISSUER = "https://dev.api.listywave.com";
    private static final Long ACCESS_TOKEN_VALID_MILLISECOND = MINUTES.toMillis(30);
    private static final Long REFRESH_TOKEN_VALID_MILLISECOND = DAYS.toMillis(14);
    public static final Long REFRESH_TOKEN_VALID_SECOND = DAYS.toSeconds(14);

    private final SecretKey secretKey;

    public JwtManager(@Value("${jwt.plain-secret-key}") String plainSecretKey) {
        byte[] encoded = Base64.encode(plainSecretKey.getBytes(UTF_8));
        secretKey = Keys.hmacShaKeyFor(encoded);
    }

    public String createAccessToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .header().type("jwt").and()
                .signWith(secretKey)
                .issuer(ISSUER)
                .issuedAt(now)
                .subject(String.valueOf(userId))
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_MILLISECOND))
                .issuedAt(Date.from(Instant.now()))
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .header().type("jwt").and()
                .signWith(secretKey)
                .issuer(ISSUER)
                .issuedAt(now)
                .subject(String.valueOf(userId))
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_MILLISECOND))
                .issuedAt(Date.from(Instant.now()))
                .compact();
    }

    public Long read(String token) {
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
