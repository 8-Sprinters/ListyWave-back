package com.listywave.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class OptionalAuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenReader tokenReader;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(OptionalAuth.class) &&
                parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest httpRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        return tokenReader.readAccessToken(httpRequest);
    }
}
