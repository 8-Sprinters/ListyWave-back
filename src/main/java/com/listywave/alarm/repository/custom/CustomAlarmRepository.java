package com.listywave.alarm.repository.custom;

import com.listywave.alarm.application.dto.AlarmResponse;
import com.listywave.user.application.domain.User;
import java.util.List;

public interface CustomAlarmRepository {
    List<AlarmResponse> getAlarms(User user);

    void deleteAlarmThirtyDaysAgo();
}
