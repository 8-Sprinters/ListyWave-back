package com.listywave.alarm.repository.custom;

import com.listywave.alarm.application.dto.FindAlarmResponse;
import com.listywave.user.application.domain.User;
import java.util.List;

public interface CustomAlarmRepository {
    List<FindAlarmResponse> getAlarms(User user);

    void deleteAlarmThirtyDaysAgo();
}
