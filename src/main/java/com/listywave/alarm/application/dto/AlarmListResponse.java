package com.listywave.alarm.application.dto;

import java.util.List;

public record AlarmListResponse(List<AlarmResponse> alarmList) {

    public static AlarmListResponse of(List<AlarmResponse> alarmList) {
        return new AlarmListResponse(alarmList);
    }
}
