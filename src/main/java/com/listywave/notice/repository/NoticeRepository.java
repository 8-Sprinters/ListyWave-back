package com.listywave.notice.repository;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.notice.application.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    default Notice getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "존재하지 않는 공지입니다."));
    }

    @Query("""
            select n
            from Notice n
            join fetch NoticeContent nc on nc.notice = n
            where n.id = :noticeId
            """)
    Notice findByIdWithFetch(Long noticeId);
}
