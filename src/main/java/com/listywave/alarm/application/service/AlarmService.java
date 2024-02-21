package com.listywave.alarm.application.service;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.alarm.application.domain.Alarm;
import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.alarm.application.dto.AlarmCheckResponse;
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
        return new AlarmListResponse(alarmRepository.getAlarms(user));
    }

    public void readAlarm(Long alarmId, String accessToken) {
        Long tokenUserId = jwtManager.read(accessToken);
        User user = userRepository.getById(tokenUserId);

        Alarm alarm = alarmRepository.findAlarmByIdAndReceiveUserId(alarmId, user.getId())
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        alarm.readAlarm();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AlarmEvent alarmEvent) {
        alarmEvent.validateDifferentPublisherAndReceiver();
        alarmRepository.save(alarmEvent.toEntity());
    }

    public AlarmCheckResponse checkAllAlarmsRead(String accessToken) {
        Long tokenUserId = jwtManager.read(accessToken);
        User user = userRepository.getById(tokenUserId);
        return new AlarmCheckResponse(alarmRepository.hasCheckedAlarmsByReceiveUserId(user.getId()));
    }
}
