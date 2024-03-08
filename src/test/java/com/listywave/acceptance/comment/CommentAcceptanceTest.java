package com.listywave.acceptance.comment;

import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.n개의_댓글_생성_요청;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_삭제_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_수정_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_저장_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_조회_API_호출;
import static com.listywave.acceptance.reply.ReplyAcceptanceTestHelper.답글_등록_API_호출;
import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.presentation.dto.request.ReplyCreateRequest;
import com.listywave.list.presentation.dto.request.comment.CommentCreateRequest;
import com.listywave.list.presentation.dto.request.comment.CommentUpdateRequest;
import com.listywave.user.application.domain.User;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 관련 인수테스트")
public class CommentAcceptanceTest extends AcceptanceTest {

    @Test
    void 댓글을_성공적으로_작성하고_조회한다() {
        // given
        User 동호 = 회원을_저장한다(동호());
        String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
        ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

        // when
        List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(11);
        댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

        // then
        ExtractableResponse<Response> response = 댓글_조회_API_호출(동호_리스트.getId());
        CommentFindResponse result = response.as(CommentFindResponse.class);

        assertThat(result.hasNext()).isTrue();
        assertThat(result.totalCount()).isEqualTo(11);
    }

    @Nested
    class 댓글_수정 {

        @Test
        void 댓글을_성공적으로_수정한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(12);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            // when
            CommentUpdateRequest request = new CommentUpdateRequest("수정할게요!");
            댓글_수정_API_호출(동호_액세스_토큰, request, 동호_리스트.getId(), 5L);

            // then
            CommentFindResponse result = 댓글_조회_API_호출(동호_리스트.getId()).as(CommentFindResponse.class);
            assertThat(result.comments().get(4).content()).isEqualTo("수정할게요!");
        }

        @Test
        void 리스트에_댓글이_존재하지_않다면_예외가_발생한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(13);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            // when
            CommentUpdateRequest request = new CommentUpdateRequest("수정할게요!");
            ExtractableResponse<Response> response = 댓글_수정_API_호출(동호_액세스_토큰, request, 동호_리스트.getId(), 101L);

            // then
            assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
        }

        @Test
        void 타인의_댓글은_수정하지_못한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(1);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(정수_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            // when
            CommentUpdateRequest request = new CommentUpdateRequest("수정할게요!");
            ExtractableResponse<Response> response = 댓글_수정_API_호출(동호_액세스_토큰, request, 동호_리스트.getId(), 1L);

            // then
            assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
        }
    }

    @Nested
    class 댓글_삭제 {

        @Test
        void 타인의_댓글을_삭제하면_예외가_발생한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            // when
            ExtractableResponse<Response> response = 댓글_삭제_API_호출(정수_액세스_토큰, 동호_리스트.getId(), 1L);

            // then
            assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
        }

        @Test
        void 답글이_달리지_않은_댓글을_성공적으로_삭제한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            // when
            댓글_삭제_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 2L);

            // then
            CommentFindResponse result = 댓글_조회_API_호출(동호_리스트.getId()).as(CommentFindResponse.class);
            assertThat(result.totalCount()).isEqualTo(2L);
        }

        @Test
        void 답글이_달린_댓글을_삭제하면_softDelete_처리_한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 댓글_생성요청));

            ReplyCreateRequest request = new ReplyCreateRequest("답글 달아요! 😀 ");
            답글_등록_API_호출(동호_액세스_토큰, request, 동호_리스트.getId(), 2L);

            // when
            댓글_삭제_API_호출(동호_액세스_토큰, 동호_리스트.getId(), 2L);

            // then
            CommentFindResponse result = 댓글_조회_API_호출(동호_리스트.getId()).as(CommentFindResponse.class);
            assertThat(result.totalCount()).isEqualTo(3);
            assertThat(result.comments().get(1).isDeleted()).isTrue();
        }
    }
}
