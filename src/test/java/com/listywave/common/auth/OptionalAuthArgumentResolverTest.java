package com.listywave.common.auth;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.auth.application.domain.JwtManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

@DisplayName("OptionalAuthArgumentResolver는 ")
class OptionalAuthArgumentResolverTest {

    private final JwtManager jwtManager = new JwtManager(
            "kasjnhfdkahxsudhfcsdubnhfxsdjhfcxs",
            "http://localhost",
            1,
            1,
            HOURS,
            HOURS
    );
    private final TokenReader tokenReader = new TokenReader(jwtManager);
    private final OptionalAuthArgumentResolver authArgumentResolver = new OptionalAuthArgumentResolver(tokenReader);
    private final NativeWebRequest webRequest = mock(NativeWebRequest.class);
    private final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

    @Test
    void MethodParameter가_Long_타입이며_OptionalAuth_어노테이션이_있으면_true를_반환한다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.hasParameterAnnotation(OptionalAuth.class)).thenReturn(true);
        doReturn(Long.class).when(methodParameter).getParameterType();

        // when
        boolean result = authArgumentResolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void MethodParameter가_Long_타입이지만_OptionalAuth_어노테이션이_없으면_false를_반환한다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.hasParameterAnnotation(OptionalAuth.class)).thenReturn(false);
        doReturn(Long.class).when(methodParameter).getParameterType();

        // when
        boolean result = authArgumentResolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void MethodParameter가_OptionalAuth_어노테이션이_있지만_Long_타입이_아니면_false를_반환한다() {
        // given
        MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.hasParameterAnnotation(OptionalAuth.class)).thenReturn(true);
        doReturn(String.class).when(methodParameter).getParameterType();

        // when
        boolean result = authArgumentResolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 액세스_토큰이_Authorization_헤더에_있으면_그_값을_읽어_userId를_반환한다() throws Exception {
        // given
        String accessToken = jwtManager.createAccessToken(2L);
        when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + accessToken);

        // when
        Object result = authArgumentResolver.resolveArgument(null, null, webRequest, null);

        // then
        assertThat((Long) result).isEqualTo(2L);
    }

    @Test
    void 액세스_토큰이_Cookie에_포함되면_그_값을_읽어_userId를_반환한다() throws Exception {
        // given
        String accessToken = jwtManager.createAccessToken(3L);
        when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(null);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{new Cookie("accessToken", accessToken)});

        // when
        Object result = authArgumentResolver.resolveArgument(null, null, webRequest, null);

        // then
        assertThat((Long) result).isEqualTo(3L);
    }

    @Test
    void 액세스_토큰이_Authorization_헤더와_Cookie에_포함되지_않으면_예외가_발생한다() throws Exception {
        // given
        when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(null);
        when(httpServletRequest.getCookies()).thenReturn(null);

        // when
        Object result = authArgumentResolver.resolveArgument(null, null, webRequest, null);

        // then
        assertThat(result).isNull();
    }
}
