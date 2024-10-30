package com.listywave.notice.application.service;

import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeContent;
import com.listywave.notice.application.service.dto.NoticeCreateRequest;
import com.listywave.notice.application.service.dto.NoticeFindAllResponseToAdmin;
import com.listywave.notice.application.service.dto.NoticeFindAllResponseToUser;
import com.listywave.notice.application.service.dto.NoticeFindResponse;
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

    @Transactional(readOnly = true)
    public List<NoticeFindAllResponseToAdmin> findAllToAdmin() {
        List<Notice> notices = noticeRepository.findAll();
        return NoticeFindAllResponseToAdmin.toList(notices);
    }

    @Transactional(readOnly = true)
    public List<NoticeFindAllResponseToUser> findAllToUser() {
        List<Notice> notices = noticeRepository.findAll();
        return NoticeFindAllResponseToUser.toList(notices);
    }

    @Transactional(readOnly = true)
    public NoticeFindResponse findOneSpecific(Long id) {
        Notice result = noticeRepository.findOne(id);
        Notice prevNotice = noticeRepository.findOne(id - 1);
        Notice nextNotice = noticeRepository.findOne(id + 1);
        return NoticeFindResponse.of(result, prevNotice, nextNotice);
    }
}
