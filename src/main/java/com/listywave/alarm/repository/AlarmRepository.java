package com.listywave.alarm.repository;

import com.listywave.alarm.application.domain.Alarm;
import com.listywave.alarm.repository.custom.CustomAlarmRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, CustomAlarmRepository {

    Optional<Alarm> findAlarmByIdAndReceiveUserId(Long id, Long receiveUserId);

    @Query("""
            select case when count(*) > 0 then false else true end
            from Alarm a
            where a.receiveUserId = :receiveUserId and a.isChecked = false
            """)
    Boolean hasCheckedAlarmsByReceiveUserId(Long receiveUserId);
}
