package com.listywave.list.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Lists;
import com.listywave.list.application.dto.response.CommentCreateResponse;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.ListRepository;
import com.listywave.list.repository.ReplyRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final JwtManager jwtManager;
    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    public CommentCreateResponse create(Long listId, String accessToken, String content) {
        Long userId = jwtManager.read(accessToken);
        User user = userRepository.getById(userId);
        Lists list = listRepository.getById(listId);

        Comment comment = Comment.create(list, user, content);
        Comment saved = commentRepository.save(comment);

        return CommentCreateResponse.of(saved, user);
    }

    public void delete(Long listId, String accessToken, Long commentId) {
        Long userId = jwtManager.read(accessToken);
        userRepository.getById(userId);
        listRepository.getById(listId);

        Comment comment = commentRepository.getById(commentId);
        if (replyRepository.existsByComment(comment)) {
            comment.softDelete();
            return;
        }
        commentRepository.delete(comment);
    }
}
