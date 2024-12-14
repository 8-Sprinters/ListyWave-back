package com.listywave.mention;

import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.common.IntegrationTest;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.list.application.dto.ReplyDeleteCommand;
import com.listywave.list.application.dto.ReplyUpdateCommand;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.application.dto.response.CommentFindResponse.CommentDto;
import com.listywave.list.application.dto.response.CommentFindResponse.MentionDto;
import com.listywave.list.application.dto.response.CommentFindResponse.ReplyDto;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MentionServiceTest extends IntegrationTest {

    @Nested
    class 멘션_생성 {

        @Test
        void 댓글_작성_시_다른_유저를_멘션할_경우_저장된다() {
            // given
            List<Long> mentionIds = List.of(js.getId(), ej.getId());

            // when
            Long savedCommentId = commentService.create(list.getId(), dh.getId(), "댓글 남겨요!", mentionIds).id();
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
            List<Long> mentionIds = List.of(dh.getId(), ej.getId());
            Long savedMentionId = replyService.create(list.getId(), savedCommentId, js.getId(), "답글 남깁니당", mentionIds).id();
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
            List<Long> mentionIds = List.of(js.getId(), ej.getId());
            Long savedCommentId = commentService.create(list.getId(), dh.getId(), "댓글이용", mentionIds).id();
            commentRepository.getById(savedCommentId);

            // when
            CommentFindResponse response = commentService.findAllBy(list.getId(), 5, null);
            CommentDto commentDto = response.comments().get(0);

            // then
            assertAll(
                    () -> assertThat(commentDto.mentions()).hasSize(2),
                    () -> assertThat(commentDto.mentions().get(0).userId()).isEqualTo(js.getId()),
                    () -> assertThat(commentDto.mentions().get(1).userId()).isEqualTo(ej.getId())
            );
        }

        @Test
        void 멘션이_없는_댓글을_조회한다() {
            // given
            Long savedCommentId = commentService.create(list.getId(), dh.getId(), "댓글이용", EMPTY_LIST).id();
            commentRepository.getById(savedCommentId);

            // when
            CommentFindResponse response = commentService.findAllBy(list.getId(), 5, null);
            CommentDto commentDto = response.comments().get(0);

            // then
            assertThat(commentDto.mentions()).isEmpty();
        }

        @Test
        void 댓글에서_멘션을_당한_사용자가_탈퇴한_사용자인_경우_id는_0이고_닉네임은_withdrawer로_응답한다() {
            // given
            commentService.create(list.getId(), dh.getId(), "댓글이용", List.of(js.getId(), ej.getId()));

            // when
            authService.withdraw(js.getId());

            // then
            CommentFindResponse response = commentService.findAllBy(list.getId(), 5, null);
            CommentDto commentDto = response.comments().get(0);
            List<MentionDto> mentions = commentDto.mentions();

            assertAll(
                    () -> assertThat(mentions).hasSize(2),
                    () -> {
                        MentionDto mentionToJs = mentions.get(0);
                        assertThat(mentionToJs.userId()).isEqualTo(0L);
                        assertThat(mentionToJs.userNickname()).isEqualTo("withdrawer");
                    },
                    () -> {
                        MentionDto mentionToEj = mentions.get(1);
                        assertThat(mentionToEj.userId()).isEqualTo(ej.getId());
                        assertThat(mentionToEj.userNickname()).isEqualTo(ej.getNickname());
                    }
            );
        }

        @Test
        void 멘션을_포함한_답글을_조회한다() {
            // given
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이용", EMPTY_LIST).id();
            List<Long> mentionIds = List.of(dh.getId());
            replyService.create(list.getId(), commentId, js.getId(), "답글이용", mentionIds);

            // when
            List<CommentDto> comments = commentService.findAllBy(list.getId(), 5, null).comments();
            ReplyDto reply = comments.get(0).replies().get(0);

            // then
            assertThat(reply.mentions()).hasSize(1);
            assertThat(reply.mentions().get(0).userId()).isEqualTo(dh.getId());
        }

        @Test
        void 멘션이_없는_답글을_조회한다() {
            // given
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이용", EMPTY_LIST).id();
            replyService.create(list.getId(), commentId, js.getId(), "답글이용", EMPTY_LIST);

            // when
            List<CommentDto> comments = commentService.findAllBy(list.getId(), 5, null).comments();
            ReplyDto reply = comments.get(0).replies().get(0);

            // then
            assertThat(reply.mentions()).isEmpty();
        }

        @Test
        void 답글에서_멘션을_당한_사용자가_탈퇴한_사용자인_경우_id는_0이고_닉네임은_withdrawer으로_응답한다() {
            // given
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이용", EMPTY_LIST).id();
            replyService.create(list.getId(), commentId, js.getId(), "답글이용", List.of(dh.getId(), ej.getId()));

            authService.withdraw(ej.getId());

            // when
            List<CommentDto> comments = commentService.findAllBy(list.getId(), 5, null).comments();
            ReplyDto reply = comments.get(0).replies().get(0);

            // then
            assertAll(
                    () -> assertThat(reply.mentions()).hasSize(2),
                    () -> assertThat(reply.mentions().get(0).userId()).isEqualTo(dh.getId()),
                    () -> {
                        MentionDto mentionToEj = reply.mentions().get(1);
                        assertThat(mentionToEj.userId()).isEqualTo(0L);
                        assertThat(mentionToEj.userNickname()).isEqualTo("withdrawer");
                    }
            );
        }
    }

    @Nested
    class 멘션_수정 {

        @Test
        void 댓글을_수정해_새로운_멘션을_추가한다() {
            // given
            List<Long> firstMentionIds = List.of(js.getId());
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이요", firstMentionIds).id();

            // when
            List<Long> newMentionIds = List.of(js.getId(), ej.getId());
            commentService.update(list.getId(), dh.getId(), commentId, "댓글 수정이요", newMentionIds);

            // then
            CommentFindResponse response = commentService.findAllBy(list.getId(), 5, null);
            List<MentionDto> result = response.comments().get(0).mentions();
            assertAll(
                    () -> assertThat(result).hasSize(2),
                    () -> assertThat(result.get(0).userId()).isEqualTo(js.getId()),
                    () -> assertThat(result.get(1).userId()).isEqualTo(ej.getId())
            );
        }

        @Test
        void 댓글을_수정해_멘션을_제거한다() {
            // given
            List<Long> mentionIds = List.of(js.getId());
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이요", mentionIds).id();

            // when
            commentService.update(list.getId(), dh.getId(), commentId, "댓글 수정이요", EMPTY_LIST);

            // then
            CommentFindResponse response = commentService.findAllBy(list.getId(), 5, null);
            List<MentionDto> result = response.comments().get(0).mentions();
            assertThat(result).isEmpty();
        }

        @Test
        void 답글을_수정해_새로운_멘션을_추가한다() {
            // given
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이요", EMPTY_LIST).id();
            List<Long> initialMentionIds = List.of(dh.getId());
            Long replyId = replyService.create(list.getId(), commentId, js.getId(), "답글이요", initialMentionIds).id();

            // when
            List<Long> newMentionIds = List.of(dh.getId(), ej.getId());
            ReplyUpdateCommand replyUpdateCommand = new ReplyUpdateCommand(list.getId(), commentId, replyId, "답글 수정이요", newMentionIds);
            replyService.update(replyUpdateCommand, js.getId());

            // then
            CommentFindResponse response = commentService.findAllBy(list.getId(), 5, null);
            ReplyDto result = response.comments().get(0).replies().get(0);
            assertAll(
                    () -> assertThat(result.content()).isEqualTo("답글 수정이요"),
                    () -> {
                        List<MentionDto> mentions = result.mentions();
                        assertThat(mentions).hasSize(2);
                        assertThat(mentions.get(0).userId()).isEqualTo(dh.getId());
                        assertThat(mentions.get(1).userId()).isEqualTo(ej.getId());
                    }
            );
        }

        @Test
        void 답글을_수정해_멘션을_제거한다() {
            // given
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이요", EMPTY_LIST).id();
            List<Long> firstMentionIds = List.of(dh.getId());
            Long replyId = replyService.create(list.getId(), commentId, js.getId(), "답글이요", firstMentionIds).id();

            // when
            ReplyUpdateCommand replyUpdateCommand = new ReplyUpdateCommand(list.getId(), commentId, replyId, "답글 수정이요", EMPTY_LIST);
            replyService.update(replyUpdateCommand, js.getId());

            // then
            CommentFindResponse response = commentService.findAllBy(list.getId(), 5, null);
            List<MentionDto> result = response.comments().get(0).replies().get(0).mentions();
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class 멘션_삭제 {

        @Test
        void 멘션을_한_댓글_삭제_시_함께_삭제한다() {
            // given
            List<Long> mentionIds = List.of(js.getId(), ej.getId());
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이요", mentionIds).id();

            // when
            commentService.delete(list.getId(), commentId, dh.getId());

            // then
            assertThat(mentionRepository.findAll()).isEmpty();
        }

        @Test
        void 멘션을_한_답글_삭제_시_함께_삭제한다() {
            // given
            Long commentId = commentService.create(list.getId(), dh.getId(), "댓글이요", EMPTY_LIST).id();
            List<Long> mentionIds = List.of(dh.getId());
            Long replyId = replyService.create(list.getId(), commentId, js.getId(), "답글이요", mentionIds).id();

            // when
            ReplyDeleteCommand command = new ReplyDeleteCommand(list.getId(), commentId, replyId);
            replyService.delete(command, js.getId());

            // then
            assertThat(mentionRepository.findAll()).isEmpty();
        }
    }
}
