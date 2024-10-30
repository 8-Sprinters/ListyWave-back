package com.listywave.notice.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    SUBTITLE,
    BODY,
    IMAGE,
    BUTTON,
    LINE,
    NOTE,
    ;
}
