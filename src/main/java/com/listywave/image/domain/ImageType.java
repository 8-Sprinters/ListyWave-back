package com.listywave.image.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageType {

    LISTS_ITEM("lists_item"),
    USER_PROFILE("user_profile"),
    USER_BACKGROUND("user_background"),
    ;

    private final String value;
}
