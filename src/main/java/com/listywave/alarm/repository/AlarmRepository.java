package com.listywave.alarm.repository;

import com.listywave.alarm.application.domain.Alarm;
import com.listywave.alarm.repository.custom.CustomAlarmRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, CustomAlarmRepository {

    Optional<Alarm> findAlarmByIdAndReceiveUserId(Long id, Long receiveUserId);
}
