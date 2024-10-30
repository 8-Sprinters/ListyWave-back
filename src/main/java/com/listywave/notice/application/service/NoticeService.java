package com.listywave.notice.application.service;

import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeContent;
import com.listywave.notice.application.service.dto.NoticeCreateRequest;
import com.listywave.notice.repository.NoticeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Long create(NoticeCreateRequest request) {
        Notice notice = request.toNotice();
        List<NoticeContent> noticeContents = request.toNoticeContents(notice);
        notice.addContents(noticeContents);
        return noticeRepository.save(notice).getId();
    }
}
