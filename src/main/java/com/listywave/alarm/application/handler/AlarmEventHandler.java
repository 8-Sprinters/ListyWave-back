package com.listywave.alarm.application.handler;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.alarm.application.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AlarmEventHandler {

    private final AlarmService alarmService;

    @TransactionalEventListener
    public void saveAlarm(AlarmEvent alarmEvent) {
        alarmService.save(alarmEvent);
    }
}
