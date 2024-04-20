package com.listywave.alarm.application.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AlarmType {

    FOLLOW("follow"),
    COLLECT("collect"),
    COMMENT("comment"),
    REPLY("reply"),
    COLLABORATOR("collaborator"),
    ;

    private final String value;
}
