package com.listywave.alarm.presentation.controller;

import com.listywave.alarm.application.dto.AlarmCheckResponse;
import com.listywave.alarm.application.dto.AlarmListResponse;
import com.listywave.alarm.application.service.AlarmService;
import com.listywave.common.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/alarms")
    ResponseEntity<AlarmListResponse> getAlarms(
            @Auth Long loginUserId
    ) {
        AlarmListResponse response = alarmService.getAlarms(loginUserId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/alarms/{alarmId}")
    ResponseEntity<Void> readAlarm(
            @PathVariable("alarmId") Long alarmId,
            @Auth Long loginUserId
    ) {
        alarmService.readAlarm(alarmId, loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alarms/check-new")
    ResponseEntity<AlarmCheckResponse> checkAllAlarmsRead(
            @Auth Long loginUserId
    ) {
        return ResponseEntity.ok().body(alarmService.checkAllAlarmsRead(loginUserId));
    }

    @PatchMapping("/alarms")
    ResponseEntity<Void> readAllAlarm(
            @Auth Long loginUserId
    ) {
        alarmService.readAllAlarm(loginUserId);
        return ResponseEntity.noContent().build();
    }
}
