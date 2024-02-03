package com.listywave.auth.application.domain;

import static com.listywave.common.exception.ErrorCode.REQUIRED_ACCESS_TOKEN;
import static java.util.concurrent.TimeUnit.MINUTES;

import com.listywave.common.exception.CustomException;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtManager {

    private static final SecretKey key = Jwts.SIG.HS256.key().build();
    private static final Long ACCESS_TOKEN_VALID_MILLISECOND = MINUTES.toMillis(30);

    public String createToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .header().type("jwt").and()
                .signWith(key)
                .issuer("https://dev.api.listywave.com")
                .issuedAt(now)
                .subject(String.valueOf(userId))
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_MILLISECOND))
                .issuedAt(Date.from(Instant.now()))
                .compact();
    }

    public Long read(String token) {
        if (token.isBlank()) {
            throw new CustomException(REQUIRED_ACCESS_TOKEN);
        }

        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }
}
