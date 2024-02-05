package com.listywave.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnvironmentConstants {

    LOCAL("local"),
    DEV("dev"),
    ;

    private final String value;
}
