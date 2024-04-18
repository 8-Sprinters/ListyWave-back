package com.listywave.common.auth;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final UriAndMethod[] whiteList = {
            new UriAndMethod("/lists/explore", GET),
            new UriAndMethod("/lists/search", GET),
            new UriAndMethod("/lists/{listId}/comments", GET),
            new UriAndMethod("/lists/upload-url", GET),
            new UriAndMethod("/lists/upload-complete", GET),
            new UriAndMethod("/lists/{listId}/histories", GET),
            new UriAndMethod("/users/{userId}/lists", GET),
            new UriAndMethod("/users/{userId}/followers", GET),
            new UriAndMethod("/users/{userId}/followings", GET),
            new UriAndMethod("/users/exists", GET),
            new UriAndMethod("/auth/kakao", GET),
            new UriAndMethod("/auth/redirect/kakao", GET),
            new UriAndMethod("/categories", GET),
            new UriAndMethod("/users/basic-profile-image", GET),
            new UriAndMethod("/users/basic-background-image", GET),
    };

    private final AuthContext authContext;
    private final TokenReader tokenReader;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isPreflight(request)) {
            return true;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (isNonRequiredAuthentication(handler)) {
            return true;
        }

        Long userId = tokenReader.readAccessToken(request);
        authContext.setUserId(userId);
        return true;
    }

    private boolean isPreflight(HttpServletRequest request) {
        return request.getMethod().equals(OPTIONS.name());
    }

    private boolean isNonRequiredAuthentication(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        String mappingUri = requestMapping.value()[0];
        HttpMethod mappingMethod = requestMapping.method()[0].asHttpMethod();

        return Arrays.stream(whiteList)
                .anyMatch(it -> it.isMatch(mappingUri, mappingMethod));
    }
}

record UriAndMethod(
        String uri,
        HttpMethod method
) {

    public boolean isMatch(String mappingUrl, HttpMethod method) {
        return this.uri.equals(mappingUrl) && this.method.equals(method);
    }
}
