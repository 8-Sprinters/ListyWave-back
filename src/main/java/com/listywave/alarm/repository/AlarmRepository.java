package com.listywave.alarm.repository;

import com.listywave.alarm.application.domain.Alarm;
import com.listywave.alarm.repository.custom.CustomAlarmRepository;
import com.listywave.list.application.domain.list.ListEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, CustomAlarmRepository {

    Optional<Alarm> findAlarmByIdAndReceiveUserId(Long id, Long receiveUserId);

    @Query("""
            select case when count(*) > 0 then false else true end
            from Alarm a
            where a.receiveUserId = :receiveUserId and a.isChecked = false
            """)
    Boolean hasCheckedAlarmsByReceiveUserId(Long receiveUserId);

    void deleteAllByList(ListEntity list);

    @Modifying
    @Query("delete from Alarm a where a.list in :lists")
    void deleteAllByListsIn(@Param("lists") List<ListEntity> lists);

    @Modifying(clearAutomatically = true)
    @Query("""
            update Alarm a
            set a.isChecked = true
            where a.receiveUserId = :receiveUserId
            and a.isChecked = false
            """)
    void readAllAlarm(Long receiveUserId);

    @Query("""
            select a
            from Alarm a
            join fetch a.sendUser u
            left join ListEntity l on a.list = l
            left join Comment c on a.comment = c
            left join Reply r on a.reply = r
            where a.receiveUserId = :receiveUserId and a.createdDate >= :thirtyDaysAgo
            """)
    List<Alarm> findAllByReceiveUserId(Long receiveUserId, LocalDateTime thirtyDaysAgo);
}
