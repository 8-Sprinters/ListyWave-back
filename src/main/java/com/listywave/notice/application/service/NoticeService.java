package com.listywave.notice.application.service;

import com.listywave.alarm.application.domain.AlarmCreateEvent;
import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeContent;
import com.listywave.notice.application.domain.NoticeDescription;
import com.listywave.notice.application.domain.NoticeTitle;
import com.listywave.notice.application.domain.NoticeType;
import com.listywave.notice.application.service.dto.NoticeCreateRequest;
import com.listywave.notice.application.service.dto.NoticeFindAllResponseToAdmin;
import com.listywave.notice.application.service.dto.NoticeFindAllResponseToUser;
import com.listywave.notice.application.service.dto.NoticeFindResponse;
import com.listywave.notice.application.service.dto.NoticeUpdateRequest;
import com.listywave.notice.repository.NoticeRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private static final String OFFICIAL_USER_NICKNAME = "ListyWave";

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

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
        Notice result = noticeRepository.findByIdWithFetch(id);
        Notice prevNotice = noticeRepository.findByIdWithFetch(id - 1);
        Notice nextNotice = noticeRepository.findByIdWithFetch(id + 1);
        return NoticeFindResponse.of(result, prevNotice, nextNotice);
    }

    public void update(NoticeUpdateRequest request, Long noticeId) {
        Notice notice = noticeRepository.findByIdWithFetch(noticeId);
        List<NoticeContent> newNoticeContents = request.toNoticeContents(notice);

        notice.update(
                NoticeType.codeOf(request.categoryCode()),
                new NoticeTitle(request.title()),
                new NoticeDescription(request.description()),
                newNoticeContents
        );
    }

    public void updateExposure(Long id) {
        Notice notice = noticeRepository.findByIdWithFetch(id);
        notice.changeExposure();
    }

    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    public void sendAlarm(Long id) {
        User officialUser = userRepository.findByNicknameValue(OFFICIAL_USER_NICKNAME);
        Notice notice = noticeRepository.getById(id);
        notice.sendAlarm();

        AlarmCreateEvent event = AlarmCreateEvent.notice(officialUser, notice);
        applicationEventPublisher.publishEvent(event);
    }
}
