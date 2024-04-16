package com.listywave.common.auth;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.auth.application.domain.JwtManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TokenReader는 ")
class TokenReaderTest {

    private final JwtManager jwtManager = new JwtManager(
            "skfhcnksduhfslkuhvfbsliduchsiludbhfsulicfhbsukchsldshdk",
            "http://localhost.com",
            1,
            1,
            HOURS,
            HOURS
    );
    private final TokenReader tokenReader = new TokenReader(jwtManager);
    private final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

    @Test
    void Authorization_헤더에_있는_토큰_값이_있으면_그_값을_읽는다() {
        // given
        String accessToken = jwtManager.createAccessToken(1L);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + accessToken);

        // when
        Long result = tokenReader.readAccessToken(httpServletRequest);

        // then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void Authorization_헤더가_아닌_쿠키에_토큰_값이_있으면_그_값을_읽는다() {
        // given
        String accessToken = jwtManager.createAccessToken(2L);
        when(httpServletRequest.getCookies())
                .thenReturn(new Cookie[]{new Cookie("accessToken", accessToken)});

        // when
        Long result = tokenReader.readAccessToken(httpServletRequest);

        // then
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void 헤더와_쿠키_모두_토큰_값이_없으면_null을_반환한다() {
        // given
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(null);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{});

        // when
        Long result = tokenReader.readAccessToken(httpServletRequest);

        // then
        assertThat(result).isNull();
    }
}
