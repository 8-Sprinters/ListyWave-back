package com.listywave.auth.application.domain;

import static com.listywave.common.exception.ErrorCode.REQUIRED_ACCESS_TOKEN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.HOURS;

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

    private static final Long ACCESS_TOKEN_VALID_MILLISECOND = HOURS.toMillis(8);
    private static final String PREFIX = "Bearer ";

    private final SecretKey secretKey;

    public JwtManager(@Value("${jwt.plain-secret-key}") String plainSecretKey) {
        byte[] encoded = Base64.encode(plainSecretKey.getBytes(UTF_8));
        secretKey = Keys.hmacShaKeyFor(encoded);
    }

    public String createToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .header().type("jwt").and()
                .signWith(secretKey)
                .issuer("https://dev.api.listywave.com")
                .issuedAt(now)
                .subject(String.valueOf(userId))
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_MILLISECOND))
                .issuedAt(Date.from(Instant.now()))
                .compact();
    }

    public Long read(String token) {
        if (token.isBlank() || !token.startsWith(PREFIX)) {
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
}
