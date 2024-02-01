package com.listywave.auth.domain;

import static java.util.Locale.ENGLISH;

public enum OauthServerType {

    KAKAO,
    ;

    public static OauthServerType fromName(String name) {
        return OauthServerType.valueOf(name.toUpperCase(ENGLISH));
    }
}
