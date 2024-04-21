package com.listywave.alarm.application.service;

import com.listywave.alarm.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmSchedulerService {

    private final AlarmRepository alarmRepository;

    @Async
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void alarmDeleteScheduler() {
        log.info("\n ============== alarmDeleteScheduler start ================ \n");
        alarmRepository.deleteAlarmThirtyDaysAgo();
        log.info("\n ============== alarmDeleteScheduler end ================ \n");
    }
}
