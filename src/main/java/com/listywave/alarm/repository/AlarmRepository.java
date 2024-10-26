package com.listywave.alarm.repository;

import com.listywave.alarm.application.domain.Alarm;
import com.listywave.alarm.repository.custom.CustomAlarmRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reply.Reply;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, CustomAlarmRepository {

    default Alarm getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Query("""
            select case when count(*) > 0 then false else true end
            from Alarm a
            where a.receiveUserId = :receiveUserId and a.isChecked = false
            """)
    Boolean isAllChecked(Long receiveUserId);

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
    void checkAll(Long receiveUserId);

    @Query("""
            select a
            from Alarm a
            join fetch a.sendUser u
            left join ListEntity l on a.list = l
            left join Comment c on a.comment = c
            left join Reply r on a.reply = r
            where a.receiveUserId = :receiveUserId and a.createdDate >= :thirtyDaysAgo
            order by a.createdDate desc
            """)
    List<Alarm> findAllBy(Long receiveUserId, LocalDateTime thirtyDaysAgo);

    List<Alarm> findAllByReply(Reply reply);

    void deleteByComment(Comment comment);

    void deleteAllByCommentAndReceiveUserId(Comment comment, Long receiveUserId);
}
