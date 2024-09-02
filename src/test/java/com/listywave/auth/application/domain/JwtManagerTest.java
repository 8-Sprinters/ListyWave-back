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
            // given
            Long userId = 312321L;

            // when
            String result = jwtManager.createAccessToken(userId);

            // then
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
        void PREFIX로_BEARER가_있는_경우와_없는_경우_모두_읽을_수_있다() {
            // given
            String accessToken = jwtManager.createAccessToken(userId);

            // when
            Long result1 = jwtManager.readTokenWithPrefix("Bearer " + accessToken);
            Long result2 = jwtManager.readTokenWithoutPrefix(accessToken);

            // then
            assertThat(result1).isEqualTo(result2).isEqualTo(userId);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void AccessToken_값이_빈_공백이거나_null이면_예외가_발생한다(String accessToken) {
            // when
            CustomException exception = assertThrows(CustomException.class, () -> jwtManager.readTokenWithPrefix(accessToken));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(REQUIRED_ACCESS_TOKEN);
        }

        @Test
        void 유효기간이_지난_액세스_토큰인_경우_예외가_발생한다() {
            // given
            JwtManager jwtManager = new JwtManager(plainSecretKey, issuer, accessTokenValidTimeDuration, refreshTokenValidTimeDuration, NANOSECONDS, refreshTokenValidTimeUnit);
            String accessToken = jwtManager.createAccessToken(userId);

            // when
            // then
            assertThatThrownBy(() -> jwtManager.readTokenWithPrefix("Bearer " + accessToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        void SecretKey가_다른_액세스_토큰인_경우_예외가_발생한다() {
            // given
            String accessToken = jwtManager.createAccessToken(userId);

            // when
            JwtManager differentSecretKeyJwtManager = new JwtManager("sfaksdlhfdsakjfhasdkfuhcasknfuhsabkdvajnfcgsankzdjhfc", issuer, 1, 1, NANOSECONDS, NANOSECONDS);

            // then
            assertThatThrownBy(() -> differentSecretKeyJwtManager.readTokenWithPrefix("Bearer " + accessToken))
                    .isInstanceOf(SignatureException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 리프레시_토큰이_null이거나_공백이면_예외를_발생한다(String refreshToken) {
            // when
            // then
            assertThatThrownBy(() -> jwtManager.readTokenWithoutPrefix(refreshToken));
        }

        @Test
        void 유효기간이_지난_리프레시_토큰인_경우_예외가_발생한다() {
            // given
            JwtManager jwtManager = new JwtManager(plainSecretKey, issuer, 1, 1, NANOSECONDS, NANOSECONDS);

            // when
            String refreshToken = jwtManager.createRefreshToken(userId);

            // then
            assertThatThrownBy(() -> jwtManager.readTokenWithoutPrefix(refreshToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        void SecretKey가_다른_리프레시_토큰인_경우_예외가_발생한다() {
            // given
            String refreshToken = jwtManager.createRefreshToken(userId);

            // when
            JwtManager otherSecretKeyJwtManager = new JwtManager(
                    "sdfklasdhfisdhfisadfhsicalndufhasdukhxaukdsadfuhsakcnfsajfhasdjkfhx",
                    issuer, 1, 1, MINUTES, MINUTES);

            // then
            assertThatThrownBy(() -> otherSecretKeyJwtManager.readTokenWithoutPrefix(refreshToken))
                    .isInstanceOf(SignatureException.class);
        }
    }
}
