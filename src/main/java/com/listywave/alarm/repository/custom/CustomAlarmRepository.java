package com.listywave.alarm.repository.custom;

import com.listywave.alarm.application.dto.AlarmFindResponse;
import com.listywave.user.application.domain.User;
import java.util.List;

public interface CustomAlarmRepository {
    List<AlarmFindResponse> findAllBy(User user);

    void deleteAlarmThirtyDaysAgo();
}
