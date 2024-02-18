package com.listywave.alarm.application.service;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.alarm.application.dto.AlarmListResponse;
import com.listywave.alarm.repository.AlarmRepository;
import com.listywave.auth.application.domain.JwtManager;
import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {

    private final JwtManager jwtManager;
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AlarmListResponse getAlarms(String accessToken) {
        Long tokenUserId = jwtManager.read(accessToken);
        User user = userRepository.getById(tokenUserId);
        return AlarmListResponse.of(alarmRepository.getAlarms(user));
    }

    public void alarmRead(Long alarmId, String accessToken) {
        Long tokenUserId = jwtManager.read(accessToken);
        User user = userRepository.getById(tokenUserId);
        alarmRepository.findAlarmByIdAndReceiveUserId(alarmId, user.getId())
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND))
                .alarmRead();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AlarmEvent alarmEvent) {
        if (alarmEvent.isDifferentPublisher()) {
            alarmRepository.save(alarmEvent.toEntity());
        }
    }
}
