package com.listywave.list.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Reply;
import com.listywave.list.application.dto.ReplyDeleteCommand;
import com.listywave.list.application.dto.response.ReplyCreateResponse;
import com.listywave.list.application.vo.Content;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.ReplyRepository;
import com.listywave.list.repository.list.ListRepository;
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

    public ReplyCreateResponse createReply(Long listId, Long commentId, String content) {
        listRepository.getById(listId);
//        Long writerId = jwtManager.read(accessToken); 개발 안정화까지 임의 주석 처리
        User user = userRepository.getById(3L);
        Comment comment = commentRepository.getById(commentId);

        Reply reply = new Reply(comment, user, new Content(content));
        Reply saved = replyRepository.save(reply);

        return ReplyCreateResponse.of(saved, comment, user);
    }

    public void delete(ReplyDeleteCommand command) {
//        Long writerId = jwtManager.read(command.accessToken()); 개발 안정화까지 임의 주석 처리
//        userRepository.getById(writerId);
        listRepository.getById(command.listId());
        Comment comment = commentRepository.getById(command.commentId());

        replyRepository.deleteById(command.replyId());

        if (!replyRepository.existsByComment(comment) && comment.isDeleted()) {
            commentRepository.delete(comment);
        }
    }
}
