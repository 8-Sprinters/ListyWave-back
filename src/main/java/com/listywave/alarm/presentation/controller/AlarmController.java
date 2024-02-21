package com.listywave.alarm.presentation.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.alarm.application.dto.AlarmCheckResponse;
import com.listywave.alarm.application.dto.AlarmListResponse;
import com.listywave.alarm.application.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/alarms")
    ResponseEntity<AlarmListResponse> getAlarms(
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        return ResponseEntity.ok(alarmService.getAlarms(accessToken));
    }

    @PatchMapping("/alarms/{alarmId}")
    ResponseEntity<Void> readAlarm(
            @PathVariable("alarmId") Long alarmId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        alarmService.readAlarm(alarmId, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alarms/check")
    ResponseEntity<AlarmCheckResponse> readAlarm(
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        return ResponseEntity.ok().body(alarmService.checkAlarm(accessToken));
    }

}
