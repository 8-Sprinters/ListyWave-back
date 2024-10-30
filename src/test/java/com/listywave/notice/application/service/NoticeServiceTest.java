package com.listywave.notice.application.service;

import static com.listywave.notice.application.domain.NoticeType.NEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.common.IntegrationTest;
import com.listywave.notice.application.domain.Notice;
import com.listywave.notice.application.service.dto.NoticeCreateRequest;
import com.listywave.notice.application.service.dto.NoticeCreateRequest.ContentDto;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NoticeServiceTest extends IntegrationTest {

    @Test
    void 공지를_생성한다() {
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
        noticeService.create(request);

        // then
        Notice result = noticeRepository.getById(1L);
        assertAll(
                () -> assertThat(result.getType()).isEqualTo(NEWS),
                () -> assertThat(result.getTitle().getValue()).isEqualTo("공지입니다"),
                () -> assertThat(result.getDescription().getValue()).isEqualTo("공지에요")
//                () -> assertThat(result.getContents().size()).isEqualTo(5)
//                () -> {
//                    List<NoticeContent> contents = result.getContents();
//
//                    ;
//                    assertAll(
//                            () -> assertThat(contents.get(0).getOrder()).isEqualTo(1),
//                            () -> assertThat(contents.get(0).getType()).isEqualTo(SUBTITLE),
//                            () -> assertThat(contents.get(0).getDescription()).isEqualTo("소제목입니다"),
//                            () -> assertThat(contents.get(0).getImageUrl())
//                                    .isEqualTo(contents.get(0).getButtonName())
//                                    .isEqualTo(contents.get(0).getButtonLink())
//                                    .isNull()
//                    );
//                }
        );
    }
}
