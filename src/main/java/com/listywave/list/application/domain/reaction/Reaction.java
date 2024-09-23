package com.listywave.list.application.domain.reaction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Reaction {

    COOL("멋져요"),
    AGREE("공감해요"),
    THANKS("감사해요")
    ;

    private final String name;
}
