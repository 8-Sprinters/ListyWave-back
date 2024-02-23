package com.listywave.image.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultBackgroundImages {

    BACKGROUND_IMAGE_A("https://image.listywave.com/basic/listywave_background_a.png"),
    ;

    private final String value;
}
