package com.listywave.notice.application.service;

import static com.listywave.notice.application.domain.ContentType.BODY;
import static com.listywave.notice.application.domain.ContentType.BUTTON;
import static com.listywave.notice.application.domain.ContentType.IMAGE;
import static com.listywave.notice.application.domain.ContentType.NOTE;
import static com.listywave.notice.application.domain.ContentType.SUBTITLE;
import static com.listywave.notice.application.domain.NoticeType.EVENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.admin.Admin;
import com.listywave.common.IntegrationTest;
import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.domain.NoticeType;
import com.listywave.notice.application.service.dto.NoticeCreateRequest;
import com.listywave.notice.application.service.dto.NoticeCreateRequest.ContentDto;
import com.listywave.notice.application.service.dto.NoticeFindAllResponseToUser;
import com.listywave.notice.application.service.dto.NoticeFindResponse;
import com.listywave.notice.application.service.dto.NoticeUpdateRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NoticeServiceTest extends IntegrationTest {

    private Admin admin;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        admin = adminRepository.save(new Admin(null, "1.2.3.4", "account", "1234"));
    }

    @Test
    void 공지를_생성_후_상세_조회한다() {
        // given
        Long id1 = noticeService.create(admin.getId(), createNoticeCreateRequest(1));
        Long id2 = noticeService.create(admin.getId(), createNoticeCreateRequest(2));
        Long id3 = noticeService.create(admin.getId(), createNoticeCreateRequest(3));

        // when
        NoticeFindResponse result = noticeService.findOneSpecific(id2);

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(id2),
                () -> assertThat(result.category()).isEqualTo(EVENT.getViewName()),
                () -> assertThat(result.title()).isEqualTo(2 + "번 째 공지입니다"),
                () -> assertThat(result.description()).isEqualTo(2 + "번 째 공지에요"),
                () -> {
                    List<NoticeFindResponse.ContentDto> contents = result.contents();
                    assertThat(contents).hasSize(5);
                    assertThat(contents.get(0).type()).isEqualTo(SUBTITLE.name().toLowerCase());
                    assertThat(contents.get(1).type()).isEqualTo(BODY.name().toLowerCase());
                    assertThat(contents.get(2).type()).isEqualTo(IMAGE.name().toLowerCase());
                    assertThat(contents.get(3).type()).isEqualTo(BUTTON.name().toLowerCase());
                    assertThat(contents.get(4).type()).isEqualTo(NOTE.name().toLowerCase());
                },
                () -> assertThat(result.prevNotice().id()).isEqualTo(id1),
                () -> assertThat(result.nextNotice().id()).isEqualTo(id3)
        );
    }

    private NoticeCreateRequest createNoticeCreateRequest(int th) {
        int categoryCode = th % NoticeType.values().length;
        if (categoryCode == 0) {
            categoryCode++;
        }

        return new NoticeCreateRequest(
                categoryCode,
                th + "번 째 공지입니다",
                th + "번 째 공지에요",
                List.of(
                        new ContentDto(1, "subtitle", "소제목입니다", null, null, null),
                        new ContentDto(2, "body", "본문입니다", null, null, null),
                        new ContentDto(3, "image", "이미지입니다", "https://image.com", null, null),
                        new ContentDto(4, "button", "버튼입니다", null, "버튼 이름", "https://buttonLink.com"),
                        new ContentDto(5, "note", "유의사항입니다", null, null, null)
                )
        );
    }

    @Test
    void 이전_혹은_다음_공지가_없으면_null이_반환된다() {
        // given
        NoticeCreateRequest request = createNoticeCreateRequest(1);

        // when
        Long id = noticeService.create(admin.getId(), request);

        // then
        NoticeFindResponse result = noticeService.findOneSpecific(id);
        assertAll(
                () -> assertThat(result.prevNotice()).isNull(),
                () -> assertThat(result.nextNotice()).isNull()
        );
    }

    @Nested
    class 사용자용_공지_전체_조회 {

        @Test
        void 노출된_공지가_없을_때_전체_조회한다() {
            // given
            NoticeCreateRequest noticeCreateRequest = createNoticeCreateRequest(1);
            noticeService.create(admin.getId(), noticeCreateRequest);

            // when
            List<NoticeFindAllResponseToUser> result = noticeService.findAllToUser();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        void 노출_처리가_된_공지를_모두_조회한다() {
            // given
            Long notice1Id = noticeService.create(admin.getId(), createNoticeCreateRequest(1));
            noticeService.create(admin.getId(), createNoticeCreateRequest(2));
            Long notice3Id = noticeService.create(admin.getId(), createNoticeCreateRequest(3));

            noticeService.updateExposure(admin.getId(), notice1Id);
            noticeService.updateExposure(admin.getId(), notice3Id);

            // when
            List<NoticeFindAllResponseToUser> result = noticeService.findAllToUser();

            // then
            assertAll(
                    () -> assertThat(result).hasSize(2),
                    () -> assertThat(result.stream()
                            .map(NoticeFindAllResponseToUser::id)
                            .toList()).containsExactly(notice1Id, notice3Id)
            );
        }
    }

    @Nested
    class 공지_수정 {

        @Test
        void 공지를_수정한다() {
            // given
            NoticeCreateRequest createRequest = createNoticeCreateRequest(1);
            Long noticeId = noticeService.create(admin.getId(), createRequest);

            // when
            NoticeUpdateRequest updateRequest = new NoticeUpdateRequest(
                    2,
                    "수정했습니다",
                    "수정했어요",
                    List.of(
                            new NoticeUpdateRequest.ContentDto(5, "subtitle", "소제목입니다", null, null, null),
                            new NoticeUpdateRequest.ContentDto(4, "body", "본문입니다", null, null, null),
                            new NoticeUpdateRequest.ContentDto(3, "image", "이미지입니다", "https://image.com", null, null),
                            new NoticeUpdateRequest.ContentDto(2, "button", "버튼입니다", null, "버튼 이름", "https://buttonLink.com"),
                            new NoticeUpdateRequest.ContentDto(1, "note", "유의사항입니다", null, null, null)
                    )
            );
            noticeService.update(admin.getId(), updateRequest, noticeId);

            // then
            NoticeFindResponse result = noticeService.findOneSpecific(noticeId);
            List<NoticeFindResponse.ContentDto> contents = result.contents();
            assertAll(
                    () -> assertThat(contents.get(0).type()).isEqualTo(SUBTITLE.name().toLowerCase()),
                    () -> assertThat(contents.get(1).type()).isEqualTo(BODY.name().toLowerCase()),
                    () -> assertThat(contents.get(2).type()).isEqualTo(IMAGE.name().toLowerCase()),
                    () -> assertThat(contents.get(3).type()).isEqualTo(BUTTON.name().toLowerCase()),
                    () -> assertThat(contents.get(4).type()).isEqualTo(NOTE.name().toLowerCase())
            );
        }

        @Test
        void 공지_노출_여부를_수정한다() {
            // given
            NoticeCreateRequest createRequest = createNoticeCreateRequest(1);
            Long noticeId = noticeService.create(admin.getId(), createRequest);

            // when
            noticeService.updateExposure(admin.getId(), noticeId);

            // then
            Notice result = noticeRepository.getById(noticeId);
            assertThat(result.isExposed()).isTrue();
        }

        @Test
        void 노출이_된_공지를_미노출로_변경한다() {
            // given
            NoticeCreateRequest createRequest = createNoticeCreateRequest(1);
            Long noticeId = noticeService.create(admin.getId(), createRequest);
            noticeService.updateExposure(admin.getId(), noticeId);

            // when
            noticeService.updateExposure(admin.getId(), noticeId);

            // then
            Notice result = noticeRepository.getById(noticeId);
            assertThat(result.isExposed()).isFalse();
        }
    }

    @Test
    void 공지_삭제() {
        // given
        NoticeCreateRequest createRequest = createNoticeCreateRequest(1);
        Long noticeId = noticeService.create(admin.getId(), createRequest);

        // when
        noticeService.delete(admin.getId(), noticeId);

        // then
        Optional<Notice> result = noticeRepository.findById(noticeId);
        assertThat(result).isEmpty();
    }
}
