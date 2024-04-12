package com.listywave.auth.config;

import com.listywave.auth.application.domain.JwtManager;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtManagerConfig {

    @Value("${jwt.plain-secret-key}")
    private String plainSecretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-valid-time-duration}")
    private int accessTokenValidTimeDuration;

    @Value("${jwt.refresh-token-valid-time-duration}")
    private int refreshTokenValidTimeDuration;

    @Value("${jwt.access-token-valid-time-unit}")
    private String accessTokenValidTimeUnit;

    @Value("${jwt.refresh-token-valid-time-unit}")
    private String refreshTokenValidTimeUnit;

    @Bean
    public JwtManager jwtManager() {
        return new JwtManager(
                plainSecretKey,
                issuer,
                accessTokenValidTimeDuration,
                refreshTokenValidTimeDuration,
                valueOf(accessTokenValidTimeUnit),
                valueOf(refreshTokenValidTimeUnit)
        );
    }

    private TimeUnit valueOf(String value) {
        return Arrays.stream(TimeUnit.values())
                .filter(timeUnit -> timeUnit.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow();
    }
}
