package com.listywave.list.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Reply;
import com.listywave.list.application.dto.response.ReplyCreateResponse;
import com.listywave.list.application.vo.Content;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.ReplyRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final JwtManager jwtManager;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    public ReplyCreateResponse createReply(Long commentId, String accessToken, String content) {
        Long writerId = jwtManager.read(accessToken);

        User user = userRepository.getById(writerId);
        Comment comment = commentRepository.getReferenceById(commentId);

        Reply reply = new Reply(comment, user, new Content(content));
        Reply saved = replyRepository.save(reply);

        return ReplyCreateResponse.of(saved, comment, user);
    }
}
