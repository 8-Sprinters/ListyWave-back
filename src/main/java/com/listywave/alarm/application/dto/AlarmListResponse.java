package com.listywave.alarm.application.dto;

import java.util.List;

public record AlarmListResponse(List<FindAlarmResponse> alarmList) {
}
