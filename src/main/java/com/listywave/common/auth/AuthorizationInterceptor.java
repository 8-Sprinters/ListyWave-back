package com.listywave.common.auth;

import static org.springframework.http.HttpMethod.GET;

import com.listywave.auth.application.domain.JwtManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final UriAndMethod[] whiteList = {
            new UriAndMethod("/users/recommend", Set.of(GET)),
            new UriAndMethod("/users/{userId}/lists", Set.of(GET)),
            new UriAndMethod("/users/{userId}", Set.of(GET)),
            new UriAndMethod("/users", Set.of(GET)),
            new UriAndMethod("/lists/search", Set.of(GET)),
            new UriAndMethod("/lists/explore", Set.of(GET)),
            new UriAndMethod("/lists/{listId}", Set.of(GET)),
            new UriAndMethod("/lists", Set.of(GET)),
            new UriAndMethod("/lists/{listId}/comments", Set.of(GET)),
            new UriAndMethod("/categories", Set.of(GET)),
            new UriAndMethod("/auth/**", Set.of(GET)),
    };

    private final JwtManager jwtManager;
    private final AuthContext authContext;
    private final ObjectProvider<PathMatcher> pathMatcherProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpMethod requestMethod = HttpMethod.valueOf(request.getMethod());

        if (isNonRequiredAuthentication(requestURI, requestMethod)) {
            return true;
        }
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long userId = jwtManager.read(accessToken);
        authContext.setUserId(userId);
        return true;
    }

    private boolean isNonRequiredAuthentication(String requestUri, HttpMethod requestMethod) {
        PathMatcher pathMatcher = pathMatcherProvider.getIfAvailable();
        return Arrays.stream(whiteList)
                .anyMatch(it -> it.isMatch(pathMatcher, requestUri, requestMethod));
    }

    private record UriAndMethod(
            String uri,
            Set<HttpMethod> methods
    ) {

        public boolean isMatch(PathMatcher pathMatcher, String requestUri, HttpMethod method) {
            return pathMatcher.match(this.uri, requestUri) && this.methods.contains(method);
        }
    }
}
