package com.listywave.acceptance.reply;

import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.n개의_댓글_생성_요청;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_삭제_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_저장_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_조회_API_호출;
import static com.listywave.acceptance.reply.ReplyAcceptanceTestHelper.답글_등록_API_호출;
import static com.listywave.acceptance.reply.ReplyAcceptanceTestHelper.답글_삭제_API_호출;
import static com.listywave.acceptance.reply.ReplyAcceptanceTestHelper.답글_수정_API_호출;
import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.presentation.dto.request.ReplyCreateRequest;
import com.listywave.list.presentation.dto.request.ReplyUpdateRequest;
import com.listywave.list.presentation.dto.request.comment.CommentCreateRequest;
import com.listywave.user.application.domain.User;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("답글 관련 인수테스트")
public class ReplyAcceptanceTest extends AcceptanceTest {

    @Test
    void 답글을_성공적으로_생성한다() {
        // given
        User 동호 = 회원을_저장한다(동호());
        String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
        ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

        List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
        댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

        // when
        ReplyCreateRequest request = new ReplyCreateRequest("답글 달아요! 😀 ");
        답글_등록_API_호출(동호_액세스_토큰, request, 동호_리스트.getId(), 2L);

        // then
        CommentFindResponse result = 댓글_조회_API_호출(동호_리스트.getId()).as(CommentFindResponse.class);
        assertThat(result.totalCount()).isEqualTo(4);
    }

    @Nested
    class 답글_수정 {

        @Test
        void 답글을_성공적으로_수정한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            ReplyCreateRequest 답글_생성_요청_데이터 = new ReplyCreateRequest("답글 달아요! 😀 ");
            답글_등록_API_호출(동호_액세스_토큰, 답글_생성_요청_데이터, 동호_리스트.getId(), 2L);

            // when
            ReplyUpdateRequest 답글_수정_요청_데이터 = new ReplyUpdateRequest("답글 수정입니다!~!@#!#");
            답글_수정_API_호출(동호_액세스_토큰, 답글_수정_요청_데이터, 동호_리스트.getId(), 2L, 1L);

            // then
            CommentFindResponse result = 댓글_조회_API_호출(동호_리스트.getId()).as(CommentFindResponse.class);
            assertThat(result.comments().get(1).replies().get(0).content()).isEqualTo("답글 수정입니다!~!@#!#");
        }

        @Test
        void 다른_사람이_작성한_답글은_수정하지_못한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            ReplyCreateRequest 답글_생성_요청_데이터 = new ReplyCreateRequest("답글 달아요! 😀 ");
            답글_등록_API_호출(정수_액세스_토큰, 답글_생성_요청_데이터, 동호_리스트.getId(), 2L);

            // when
            ExtractableResponse<Response> response = 답글_삭제_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 2L, 1L);

            // then
            assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
        }
    }

    @Nested
    class 답글_삭제 {

        @Test
        void 답글을_성공적으로_삭제한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            ReplyCreateRequest request = new ReplyCreateRequest("답글 달아요! 😀 ");
            답글_등록_API_호출(동호_액세스_토큰, request, 동호_리스트.getId(), 2L);

            // when
            ExtractableResponse<Response> response = 답글_삭제_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 2L, 1L);

            // then
            CommentFindResponse 전체_댓글 = 댓글_조회_API_호출(동호_리스트.getId()).as(CommentFindResponse.class);

            assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
            assertThat(전체_댓글.totalCount()).isEqualTo(3);
            assertThat(전체_댓글.comments().get(1).replies()).isEmpty();
        }

        @Test
        void 타인의_답글을_삭제하면_예외가_발생한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            ReplyCreateRequest 답글_생성_요청_데이터 = new ReplyCreateRequest("답글 달아요! 😀 ");
            답글_등록_API_호출(정수_액세스_토큰, 답글_생성_요청_데이터, 동호_리스트.getId(), 2L);

            // when
            ExtractableResponse<Response> response = 답글_삭제_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 2L, 1L);

            // then
            assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
        }

        @Test
        void 삭제_처리된_댓글에_1개_남은_답글을_삭제하면_해당_댓글도_Hard_Delete_된다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            ReplyCreateRequest request = new ReplyCreateRequest("답글 달아요! 😀 ");
            답글_등록_API_호출(동호_액세스_토큰, request, 동호_리스트.getId(), 2L);

            댓글_삭제_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 2L);

            // when
            답글_삭제_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 2L, 1L);

            // then
            CommentFindResponse result = 댓글_조회_API_호출(동호_리스트.getId()).as(CommentFindResponse.class);
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.comments().get(0).id()).isEqualTo(1);
            assertThat(result.comments().get(1).id()).isEqualTo(3);
        }
    }
}
