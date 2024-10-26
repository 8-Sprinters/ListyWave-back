package com.listywave.alarm.presentation.controller;

import com.listywave.alarm.application.dto.AlarmCheckResponse;
import com.listywave.alarm.application.dto.AlarmFindResponse;
import com.listywave.alarm.application.service.AlarmService;
import com.listywave.common.auth.Auth;
import com.listywave.user.application.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final UserService userService;
    private final AlarmService alarmService;

    @GetMapping("/alarms")
    ResponseEntity<List<AlarmFindResponse>> findAllBy(@Auth Long loginUserId) {
        List<AlarmFindResponse> response = alarmService.findAllBy(loginUserId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/alarms/{alarmId}")
    ResponseEntity<Void> check(@PathVariable("alarmId") Long alarmId, @Auth Long userId) {
        userService.getById(userId);
        alarmService.check(alarmId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/alarms/check-new")
    ResponseEntity<AlarmCheckResponse> isAllChecked(@Auth Long userId) {
        AlarmCheckResponse result = alarmService.isAllChecked(userId);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/alarms")
    ResponseEntity<Void> checkAll(@Auth Long userId) {
        alarmService.checkAll(userId);
        return ResponseEntity.noContent().build();
    }
}
