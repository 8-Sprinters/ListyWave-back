package com.listywave.acceptance.comment;

import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.n개의_댓글_생성_요청;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_삭제_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_수정_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_저장_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_조회_API_호출;
import static com.listywave.acceptance.common.CommonAcceptanceHelper.HTTP_상태_코드를_검증한다;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.가장_좋아하는_견종_TOP3_생성_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트_저장_API_호출;
import static com.listywave.acceptance.reply.ReplyAcceptanceTestHelper.답글_등록_API_호출;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.presentation.dto.request.ReplyCreateRequest;
import com.listywave.list.presentation.dto.request.comment.CommentUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 관련 인수테스트")
public class CommentAcceptanceTest extends AcceptanceTest {

    @Test
    void 댓글을_성공적으로_작성하고_조회한다() {
        // given
        var 동호 = 회원을_저장한다(동호());
        var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
        var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                .as(ListCreateResponse.class)
                .listId();

        // when
        var 댓글_생성_요청들 = n개의_댓글_생성_요청(11);
        댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트_ID, 댓글_생성요청));

        // then
        var 결과 = 댓글_조회_API_호출(동호_리스트_ID).as(CommentFindResponse.class);

        assertThat(결과.hasNext()).isTrue();
        assertThat(결과.totalCount()).isEqualTo(11);
    }

    @Nested
    class 댓글_수정 {

        @Test
        void 댓글을_성공적으로_수정한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 댓글_생성_요청들 = n개의_댓글_생성_요청(12);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트_ID, 댓글_생성요청));

            // when
            var 댓글_수정_요청 = new CommentUpdateRequest("수정할게요!");
            댓글_수정_API_호출(동호_액세스_토큰, 댓글_수정_요청, 동호_리스트_ID, 5L);

            // then
            var 댓글_조회_결과 = 댓글_조회_API_호출(동호_리스트_ID).as(CommentFindResponse.class);
            assertThat(댓글_조회_결과.comments().get(4).content()).isEqualTo("수정할게요!");
        }

        @Test
        void 리스트에_댓글이_존재하지_않다면_예외가_발생한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 댓글_생성_요청들 = n개의_댓글_생성_요청(13);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트_ID, 댓글_생성요청));

            // when
            var 댓글_수정_요청 = new CommentUpdateRequest("수정할게요!");
            var 댓글_수정_응답 = 댓글_수정_API_호출(동호_액세스_토큰, 댓글_수정_요청, 동호_리스트_ID, 101L);

            // then
            HTTP_상태_코드를_검증한다(댓글_수정_응답, NOT_FOUND);
        }

        @Test
        void 타인의_댓글은_수정하지_못한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 댓글_생성_요청들 = n개의_댓글_생성_요청(1);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(정수_액세스_토큰, 동호_리스트_ID, 댓글_생성요청));

            // when
            var 댓글_수정_요청 = new CommentUpdateRequest("수정할게요!");
            var 댓글_수정_응답 = 댓글_수정_API_호출(동호_액세스_토큰, 댓글_수정_요청, 동호_리스트_ID, 1L);

            // then
            HTTP_상태_코드를_검증한다(댓글_수정_응답, FORBIDDEN);
        }
    }

    @Nested
    class 댓글_삭제 {

        @Test
        void 타인의_댓글을_삭제하면_예외가_발생한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트_ID, 댓글_생성요청));

            // when
            var 댓글_삭제_응답 = 댓글_삭제_API_호출(정수_액세스_토큰, 동호_리스트_ID, 1L);

            // then
            HTTP_상태_코드를_검증한다(댓글_삭제_응답, FORBIDDEN);
        }

        @Test
        void 답글이_달리지_않은_댓글을_성공적으로_삭제한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트_ID, 댓글_생성요청));

            // when
            댓글_삭제_API_호출(동호_액세스_토큰, 동호_리스트_ID, 2L);
            var 댓글_조회_결과 = 댓글_조회_API_호출(동호_리스트_ID).as(CommentFindResponse.class);

            // then
            assertThat(댓글_조회_결과.totalCount()).isEqualTo(2L);
        }

        @Test
        void 답글이_달린_댓글을_삭제하면_softDelete_처리_한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 댓글_생성_요청들 = n개의_댓글_생성_요청(3);
            댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_액세스_토큰, 동호_리스트_ID, 댓글_생성요청));

            var 답글_수정_요청 = new ReplyCreateRequest("답글 달아요! 😀 ", List.of());
            답글_등록_API_호출(동호_액세스_토큰, 답글_수정_요청, 동호_리스트_ID, 2L);

            // when
            댓글_삭제_API_호출(동호_액세스_토큰, 동호_리스트_ID, 2L);
            var 댓글_조회_결과 = 댓글_조회_API_호출(동호_리스트_ID).as(CommentFindResponse.class);

            // then
            assertThat(댓글_조회_결과.totalCount()).isEqualTo(3);
            assertThat(댓글_조회_결과.comments().get(1).isDeleted()).isTrue();
        }
    }
}
