package com.listywave.list.application.service;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.comment.CommentContent;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.list.application.dto.ReplyDeleteCommand;
import com.listywave.list.application.dto.ReplyUpdateCommand;
import com.listywave.list.application.dto.response.ReplyCreateResponse;
import com.listywave.list.repository.comment.CommentRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.mention.Mention;
import com.listywave.mention.MentionService;
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
public class ReplyService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final MentionService mentionService;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ReplyCreateResponse create(
            Long listId,
            Long targetCommentId,
            Long writerId,
            String content,
            List<Long> mentionedIds
    ) {
        listRepository.getById(listId);
        User user = userRepository.getById(writerId);
        Comment comment = commentRepository.getById(targetCommentId);

        List<Mention> mentions = mentionService.toMentions(mentionedIds);
        Reply reply = replyRepository.save(new Reply(comment, user, new CommentContent(content), mentions));

        applicationEventPublisher.publishEvent(AlarmEvent.reply(comment, reply));
        return ReplyCreateResponse.of(reply, comment, user);
    }

    public void delete(ReplyDeleteCommand command, Long userId) {
        listRepository.getById(command.listId());
        User user = userRepository.getById(userId);
        Comment comment = commentRepository.getById(command.commentId());
        Reply reply = replyRepository.getById(command.replyId());

        if (!reply.isOwner(user)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS, "답글은 작성자만 삭제할 수 있습니다.");
        }

        replyRepository.deleteById(command.replyId());
        if (!replyRepository.existsByComment(comment) && comment.isDeleted()) {
            commentRepository.delete(comment);
        }
    }

    public void update(ReplyUpdateCommand command, Long writerId) {
        listRepository.getById(command.listId());
        User user = userRepository.getById(writerId);
        commentRepository.getById(command.commentId());
        Reply reply = replyRepository.getById(command.replyId());

        if (!reply.isOwner(user)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS, "답글은 작성자만 수정할 수 있습니다.");
        }
        List<Mention> mentions = mentionService.toMentions(command.mentionIds());
        reply.update(new CommentContent(command.content()), mentions);
    }
}
