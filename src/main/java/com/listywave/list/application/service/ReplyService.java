package com.listywave.list.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Reply;
import com.listywave.list.application.dto.ReplyDeleteCommand;
import com.listywave.list.application.dto.response.ReplyCreateResponse;
import com.listywave.list.application.vo.Content;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final JwtManager jwtManager;
    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    public ReplyCreateResponse createReply(Long listId, Long commentId, String content, String accessToken) {
        listRepository.getById(listId);
        Long userId = jwtManager.read(accessToken);
        User user = userRepository.getById(userId);
        Comment comment = commentRepository.getById(commentId);

        Reply reply = new Reply(comment, user, new Content(content));
        Reply saved = replyRepository.save(reply);

        return ReplyCreateResponse.of(saved, comment, user);
    }

    public void delete(ReplyDeleteCommand command, String accessToken) {
        listRepository.getById(command.listId());
        Long userId = jwtManager.read(accessToken);
        User user = userRepository.getById(userId);
        Comment comment = commentRepository.getById(command.commentId());
        Reply reply = replyRepository.getById(command.replyId());

        if (!reply.canDeleteBy(user)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS, "답글은 작성자만 삭제할 수 있습니다.");
        }
        
        replyRepository.deleteById(command.replyId());
        if (!replyRepository.existsByComment(comment) && comment.isDeleted()) {
            commentRepository.delete(comment);
        }
    }
}
