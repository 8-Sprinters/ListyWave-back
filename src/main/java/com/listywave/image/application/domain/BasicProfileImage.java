package com.listywave.image.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BasicProfileImage {

    PROFILE_IMAGE_A("https://image.listywave.com/basic/listywave_profile_a.png"),
    ;

    private final String value;

}
