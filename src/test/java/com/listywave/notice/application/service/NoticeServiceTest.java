package com.listywave.notice.application.service;

import static com.listywave.notice.application.domain.ContentType.BODY;
import static com.listywave.notice.application.domain.ContentType.BUTTON;
import static com.listywave.notice.application.domain.ContentType.IMAGE;
import static com.listywave.notice.application.domain.ContentType.NOTE;
import static com.listywave.notice.application.domain.ContentType.SUBTITLE;
import static com.listywave.notice.application.domain.NoticeType.EVENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.common.IntegrationTest;
import com.listywave.notice.application.service.dto.NoticeCreateRequest;
import com.listywave.notice.application.service.dto.NoticeCreateRequest.ContentDto;
import com.listywave.notice.application.service.dto.NoticeFindResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NoticeServiceTest extends IntegrationTest {

    @Test
    void 공지를_생성_후_상세_조회한다() {
        // given
        NoticeCreateRequest request1 = new NoticeCreateRequest(
                1,
                "첫 번째 공지입니다",
                "첫 번째 공지에요",
                List.of(
                        new ContentDto(1, "subtitle", "소제목입니다", null, null, null),
                        new ContentDto(2, "body", "본문입니다", null, null, null),
                        new ContentDto(3, "image", "이미지입니다", "https://image.com", null, null),
                        new ContentDto(4, "button", "버튼입니다", null, "버튼 이름", "https://buttonLink.com"),
                        new ContentDto(5, "note", "유의사항입니다", null, null, null)
                )
        );
        NoticeCreateRequest request2 = new NoticeCreateRequest(
                2,
                "두 번째 공지입니다",
                "두 번째 공지에요",
                List.of(
                        new ContentDto(1, "subtitle", "소제목입니다", null, null, null),
                        new ContentDto(2, "body", "본문입니다", null, null, null),
                        new ContentDto(3, "image", "이미지입니다", "https://image.com", null, null),
                        new ContentDto(4, "button", "버튼입니다", null, "버튼 이름", "https://buttonLink.com"),
                        new ContentDto(5, "note", "유의사항입니다", null, null, null)
                )
        );
        NoticeCreateRequest request3 = new NoticeCreateRequest(
                3,
                "세 번째 공지입니다",
                "세 번째 공지에요",
                List.of(
                        new ContentDto(1, "subtitle", "소제목입니다", null, null, null),
                        new ContentDto(2, "body", "본문입니다", null, null, null),
                        new ContentDto(3, "image", "이미지입니다", "https://image.com", null, null),
                        new ContentDto(4, "button", "버튼입니다", null, "버튼 이름", "https://buttonLink.com"),
                        new ContentDto(5, "note", "유의사항입니다", null, null, null)
                )
        );

        // when
        Long id1 = noticeService.create(request1);
        Long id2 = noticeService.create(request2);
        Long id3 = noticeService.create(request3);

        // then
        NoticeFindResponse result = noticeService.findOneSpecific(id2);
        assertAll(
                () -> assertThat(result.id()).isEqualTo(id2),
                () -> assertThat(result.category()).isEqualTo(EVENT.getViewName()),
                () -> assertThat(result.title()).isEqualTo("두 번째 공지입니다"),
                () -> assertThat(result.description()).isEqualTo("두 번째 공지에요"),
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

    @Test
    void 이전_혹은_다음_공지가_없으면_null이_반환된다() {
        // given
        NoticeCreateRequest request = new NoticeCreateRequest(
                1,
                "공지입니다",
                "공지에요",
                List.of(
                        new ContentDto(1, "subtitle", "소제목입니다", null, null, null),
                        new ContentDto(2, "body", "본문입니다", null, null, null),
                        new ContentDto(3, "image", "이미지입니다", "https://image.com", null, null),
                        new ContentDto(4, "button", "버튼입니다", null, "버튼 이름", "https://buttonLink.com"),
                        new ContentDto(5, "note", "유의사항입니다", null, null, null)
                )
        );

        // when
        Long id = noticeService.create(request);

        // then
        NoticeFindResponse result = noticeService.findOneSpecific(id);
        assertAll(
                () -> assertThat(result.prevNotice()).isNull(),
                () -> assertThat(result.nextNotice()).isNull()
        );
    }
}
