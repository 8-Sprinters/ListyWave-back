package com.listywave.common.auth;

import static com.listywave.common.exception.ErrorCode.REQUIRED_ACCESS_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.common.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.resource.ResourceWebHandler;

@DisplayName("AuthorizationInterceptor는 ")
class AuthorizationInterceptorTest {

    private JwtManager jwtManager;
    private AuthContext authContext;
    private TokenReader tokenReader;
    private AuthorizationInterceptor authorizationInterceptor;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    @BeforeEach
    void setUp() {
        jwtManager = mock(JwtManager.class);
        authContext = new AuthContext();
        tokenReader = new TokenReader(jwtManager);
        authorizationInterceptor = new AuthorizationInterceptor(authContext, tokenReader);
        httpServletRequest = mock(HttpServletRequest.class);
        httpServletResponse = mock(HttpServletResponse.class);
    }

    @Test
    void Authorization_헤더에_담긴_액세스_토큰을_읽어_AuthContext에_userId를_저장한다() throws Exception {
        // given
        when(httpServletRequest.getMethod()).thenReturn(GET.name());
        HandlerMethod handler = mock(HandlerMethod.class);

        RequestMapping requestMapping = mock(RequestMapping.class);
        when(handler.getMethodAnnotation(RequestMapping.class)).thenReturn(requestMapping);
        when(requestMapping.value()).thenReturn(new String[]{"my"});
        when(requestMapping.method()).thenReturn(new RequestMethod[]{RequestMethod.GET});

        String accessToken = "dufhaslndckhfksdjhkscjghacs.dkjfhnxasjdfhsjh.snvkjghc";
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(accessToken);
        when(jwtManager.readTokenWithPrefix(accessToken)).thenReturn(1L);

        // when
        authorizationInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);

        // then
        assertThat(authContext.getUserId()).isEqualTo(1L);
    }

    @Test
    void 액세스_토큰이_Authorization_헤더가_아닌_Cookie에_담기면_쿠키값을_읽는다() throws Exception {
        // given
        when(httpServletRequest.getMethod()).thenReturn(GET.name());
        HandlerMethod handler = mock(HandlerMethod.class);

        RequestMapping requestMapping = mock(RequestMapping.class);
        when(handler.getMethodAnnotation(RequestMapping.class)).thenReturn(requestMapping);
        when(requestMapping.value()).thenReturn(new String[]{"my"});
        when(requestMapping.method()).thenReturn(new RequestMethod[]{RequestMethod.GET});

        String accessToken = "dufhaslndckhfksdjhkscjghacs.dkjfhnxasjdfhsjh.snvkjghc";
        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(null);
        Cookie[] cookies = {new Cookie("accessToken", accessToken)};
        when(httpServletRequest.getCookies()).thenReturn(cookies);
        when(jwtManager.readTokenWithoutPrefix(accessToken)).thenReturn(1L);

        // when
        authorizationInterceptor.preHandle(httpServletRequest, httpServletResponse, handler);

        // then
        assertThat(authContext.getUserId()).isEqualTo(1L);
    }

    @Test
    void 인증이_필요한_리소스에_접근하지만_인증_정보가_없으면_예외가_발생한다() throws Exception {
        // given
        authorizationInterceptor = new AuthorizationInterceptor(authContext, tokenReader);

        when(httpServletRequest.getMethod()).thenReturn(GET.name());
        HandlerMethod handler = mock(HandlerMethod.class);

        RequestMapping requestMapping = mock(RequestMapping.class);
        when(handler.getMethodAnnotation(RequestMapping.class)).thenReturn(requestMapping);
        when(requestMapping.value()).thenReturn(new String[]{"my"});
        when(requestMapping.method()).thenReturn(new RequestMethod[]{RequestMethod.GET});

        when(httpServletRequest.getHeader(AUTHORIZATION)).thenReturn(null);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{});

        // when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> authorizationInterceptor.preHandle(httpServletRequest, httpServletResponse, handler)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(REQUIRED_ACCESS_TOKEN);
    }

    @Test
    void 요청_메서드가_OPTIONS_이면_값을_읽지_않는다() throws Exception {
        // given
        when(httpServletRequest.getMethod()).thenReturn(OPTIONS.name());

        // when
        authorizationInterceptor.preHandle(httpServletRequest, httpServletResponse, mock(Object.class));

        // then
        assertThat(authContext.getUserId()).isNull();
    }

    @Test
    void 핸들러_타입이_HandlerMethod가_아니면_값을_읽지_않는다() throws Exception {
        // given
        when(httpServletRequest.getMethod()).thenReturn(GET.name());

        // when
        authorizationInterceptor.preHandle(httpServletRequest, httpServletResponse, new ResourceWebHandler());

        // then
        assertThat(authContext.getUserId()).isNull();
    }
}
