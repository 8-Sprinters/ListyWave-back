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
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
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
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ReplyCreateResponse createReply(Long listId, Long commentId, String content, Long loginUserId) {
        listRepository.getById(listId);
        User user = userRepository.getById(loginUserId);
        Comment comment = commentRepository.getById(commentId);

        Reply reply = new Reply(comment, user, new CommentContent(content));
        Reply saved = replyRepository.save(reply);

        applicationEventPublisher.publishEvent(AlarmEvent.reply(comment, saved));
        return ReplyCreateResponse.of(saved, comment, user);
    }

    public void delete(ReplyDeleteCommand command, Long loginUserId) {
        listRepository.getById(command.listId());
        User user = userRepository.getById(loginUserId);
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

    public void update(ReplyUpdateCommand command, Long loginUserId) {
        listRepository.getById(command.listId());
        User user = userRepository.getById(loginUserId);
        commentRepository.getById(command.commentId());
        Reply reply = replyRepository.getById(command.replyId());

        if (!reply.isOwner(user)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS, "답글은 작성자만 수정할 수 있습니다.");
        }
        reply.update(new CommentContent(command.content()));
    }
}
