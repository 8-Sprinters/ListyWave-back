package com.listywave.user.application.service;

import com.listywave.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDeleteBatchService {

    private final UserRepository userRepository;

    @Async
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteUsers30DaysAgo() {
        log.info("\n ============== UserDeleteScheduler Start ================ \n");
        userRepository.deleteNDaysAgo(30);
        log.info("\n ============== UserDeleteScheduler End ================ \n");
    }
}
