package com.listywave.auth.application.domain;

import static com.listywave.common.exception.ErrorCode.REQUIRED_ACCESS_TOKEN;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("JwtManager는 ")
class JwtManagerTest {

    private String plainSecretKey;
    private String issuer;
    private int accessTokenValidTimeDuration;
    private TimeUnit accessTokenValidTimeUnit;
    private int refreshTokenValidTimeDuration;
    private TimeUnit refreshTokenValidTimeUnit;
    private JwtManager jwtManager;

    @BeforeEach
    void setUp() {
        plainSecretKey = "aaadadasdsadsadsadasdasdasdasdaaadadasdsadsadsadasdasdasdasdaaadadasdsadsadsadasdasdasdasd";
        issuer = "http://localhost.com";
        accessTokenValidTimeDuration = 1;
        refreshTokenValidTimeDuration = 1;
        accessTokenValidTimeUnit = MINUTES;
        refreshTokenValidTimeUnit = MINUTES;
        jwtManager = new JwtManager(
                plainSecretKey,
                issuer,
                accessTokenValidTimeDuration,
                refreshTokenValidTimeDuration,
                accessTokenValidTimeUnit,
                refreshTokenValidTimeUnit
        );
    }

    @Nested
    class 토큰을_발급한다 {

        @Test
        void userId로_액세스_토큰을_발급한다() {
            Long userId = 312321L;

            String result = jwtManager.createAccessToken(userId);

            assertThat(result).isNotNull();
            assertThat(result).isNotBlank();
        }

        @Test
        void userId로_리프레시_토큰을_발급한다() {
            Long userId = 1L;

            String result = jwtManager.createRefreshToken(userId);

            assertThat(result).isNotNull();
            assertThat(result).isNotBlank();
        }
    }

    @Nested
    class 토큰을_읽는다 {

        private final Long userId = 1L;

        @Test
        void PREFIX로_BEARER가_없는_경우_예외가_발생한다() {
            String accessToken = jwtManager.createAccessToken(userId);

            CustomException exception = assertThrows(CustomException.class, () -> jwtManager.readTokenWithPrefix(accessToken));

            assertThat(exception.getErrorCode()).isEqualTo(REQUIRED_ACCESS_TOKEN);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void AccessToken_값이_빈_공백이거나_null이면_예외가_발생한다(String accessToken) {
            CustomException exception = assertThrows(CustomException.class, () -> jwtManager.readTokenWithPrefix(accessToken));

            assertThat(exception.getErrorCode()).isEqualTo(REQUIRED_ACCESS_TOKEN);
        }

        @Test
        void 액세스_토큰을_읽고_userId를_반환한다() {
            String accessToken = jwtManager.createAccessToken(userId);

            Long result = jwtManager.readTokenWithPrefix("Bearer " + accessToken);

            assertThat(result).isEqualTo(userId);
        }

        @Test
        void 유효기간이_지난_액세스_토큰인_경우_예외가_발생한다() {
            JwtManager jwtManager = new JwtManager(plainSecretKey, issuer, accessTokenValidTimeDuration, refreshTokenValidTimeDuration, NANOSECONDS, refreshTokenValidTimeUnit);
            String accessToken = jwtManager.createAccessToken(userId);

            assertThatThrownBy(() -> jwtManager.readTokenWithPrefix("Bearer " + accessToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        void SecretKey가_다른_액세스_토큰인_경우_예외가_발생한다() {
            String accessToken = jwtManager.createAccessToken(userId);

            JwtManager differentSecretKeyJwtManager = new JwtManager("sfaksdlhfdsakjfhasdkfuhcasknfuhsabkdvajnfcgsankzdjhfc", issuer, 1, 1, NANOSECONDS, NANOSECONDS);

            assertThatThrownBy(() -> differentSecretKeyJwtManager.readTokenWithPrefix("Bearer " + accessToken))
                    .isInstanceOf(SignatureException.class);
        }

        @Test
        void 리프레시_토큰을_읽고_userId를_반환한다() {
            String refreshToken = jwtManager.createRefreshToken(userId);

            assertThat(refreshToken).isNotNull();
            assertThat(refreshToken).isNotBlank();
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 리프레시_토큰이_null이거나_공백이면_예외를_발생한다(String refreshToken) {
            assertThatThrownBy(() -> jwtManager.readTokenWithoutPrefix(refreshToken));
        }

        @Test
        void 유효기간이_지난_리프레시_토큰인_경우_예외가_발생한다() {
            JwtManager jwtManager = new JwtManager(plainSecretKey, issuer, 1, 1, NANOSECONDS, NANOSECONDS);
            String refreshToken = jwtManager.createRefreshToken(userId);

            assertThatThrownBy(() -> jwtManager.readTokenWithoutPrefix(refreshToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        void SecretKey가_다른_리프레시_토큰인_경우_예외가_발생한다() {
            String refreshToken = jwtManager.createRefreshToken(userId);

            JwtManager otherSecretKeyJwtManager = new JwtManager(
                    "sdfklasdhfisdhfisadfhsicalndufhasdukhxaukdsadfuhsakcnfsajfhasdjkfhx",
                    issuer, 1, 1, MINUTES, MINUTES);

            assertThatThrownBy(() -> otherSecretKeyJwtManager.readTokenWithoutPrefix(refreshToken))
                    .isInstanceOf(SignatureException.class);
        }
    }
}
