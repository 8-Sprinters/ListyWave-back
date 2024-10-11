package com.listywave.mention;

import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.common.IntegrationTest;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.application.dto.response.CommentFindResponse.CommentDto;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MentionServiceTest extends IntegrationTest {

    @Nested
    class 멘션_생성 {

        @Test
        void 댓글_작성_시_다른_유저를_멘션할_경우_저장된다() {
            // when
            Long savedCommentId = commentService.create(list.getId(), dh.getId(), "댓글 남겨요!", List.of(js.getId(), ej.getId())).id();
            Comment comment = commentRepository.getById(savedCommentId);

            // then
            List<Mention> result = mentionRepository.findAllByComment(comment);
            assertAll(
                    () -> assertThat(result).hasSize(2),
                    () -> assertThat(result.get(0).getUser().getId()).isEqualTo(js.getId()),
                    () -> assertThat(result.get(1).getUser().getId()).isEqualTo(ej.getId())
            );
        }

        @Test
        void 댓글_작성_시_멘션이_없을_경우_아무_값이_저장되지_않는다() {
            // when
            Long savedCommentId = commentService.create(list.getId(), dh.getId(), "댓글 남겨요!", EMPTY_LIST).id();
            Comment comment = commentRepository.getById(savedCommentId);

            // then
            List<Mention> result = mentionRepository.findAllByComment(comment);
            assertThat(result).isEmpty();
        }

        @Test
        void 답글_작성_시_다른_유저를_멘션할_경우_저장된다() {
            // given
            Long savedCommentId = commentService.create(list.getId(), dh.getId(), "댓글 남겨요!", EMPTY_LIST).id();

            // when
            Long savedMentionId = replyService.create(list.getId(), savedCommentId, js.getId(), "답글 남깁니당", List.of(dh.getId(), ej.getId())).id();
            Reply reply = replyRepository.getById(savedMentionId);

            // then
            List<Mention> result = mentionRepository.findAllByReply(reply);
            assertAll(
                    () -> assertThat(result).hasSize(2),
                    () -> assertThat(result.get(0).getUser().getId()).isEqualTo(dh.getId()),
                    () -> assertThat(result.get(1).getUser().getId()).isEqualTo(ej.getId())
            );
        }

        @Test
        void 답글_작성_시_멘션이_없을_경우_아무_값이_저장되지_않는다() {
            // given
            Long savedCommentId = commentService.create(list.getId(), dh.getId(), "댓글 남겨요!", EMPTY_LIST).id();

            // when
            Long savedMentionId = replyService.create(list.getId(), savedCommentId, js.getId(), "답글 남깁니당", EMPTY_LIST).id();
            Reply reply = replyRepository.getById(savedMentionId);

            // then
            List<Mention> result = mentionRepository.findAllByReply(reply);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class 멘션_조회 {

        @Test
        void 멘션을_포함한_댓글을_조회한다() {
            // given
            Long savedCommentId = commentService.create(list.getId(), dh.getId(), "댓글이용", List.of(js.getId(), ej.getId())).id();
            Comment comment = commentRepository.getById(savedCommentId);

            // when
            CommentFindResponse response = commentService.findCommentBy(list.getId(), 5, null);
            CommentDto commentDto = response.comments().get(0);

            // then

        }

        @Test
        void 멘션이_없는_댓글을_조회한다() {
            // given

            // when

            // then
        }

        @Test
        void 멘션을_당한_사용자가_탈퇴한_사용자인_경우_조회하지_않는다() {

        }

        @Test
        void 멘션을_포함한_답글을_조회한다() {
            // given

            // when

            // then
        }

        @Test
        void 멘션이_없는_답글을_조회한다() {
            // given

            // when

            // then
        }

        @Test
        void 답글에서_멘션을_당한_사용자가_탈퇴한_사용자인_경우_조회하지_않는다() {
            // given

            // when

            // then
        }
    }
}
