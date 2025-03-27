package com.listywave.notice.repository;

import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeContent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeContentRepository extends JpaRepository<NoticeContent, Long> {

    Optional<NoticeContent> findByNoticeAndOrder(Notice notice, int order);
}
