package com.listywave.auth.config;

import com.listywave.auth.infra.kakao.KakaoOauthApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    public KakaoOauthApiClient kakaoClient() {
        return createHttpInterface(KakaoOauthApiClient.class);
    }

    private <T> T createHttpInterface(Class<T> clazz) {
        WebClient webClient = WebClient.create();
        HttpServiceProxyFactory build = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();
        return build.createClient(clazz);
    }
}
