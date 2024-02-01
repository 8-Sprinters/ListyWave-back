package com.listywave.auth.config;

import com.listywave.auth.application.domain.OauthServerType;
import org.springframework.core.convert.converter.Converter;

public class OAuthServerTypeConverter implements Converter<String, OauthServerType> {

    @Override
    public OauthServerType convert(String source) {
        return OauthServerType.fromName(source);
    }
}
