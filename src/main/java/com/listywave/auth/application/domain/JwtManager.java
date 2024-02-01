package com.listywave.auth.application.domain;

import static java.util.concurrent.TimeUnit.MINUTES;

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
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }
}
