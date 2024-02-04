package com.listywave.list.application.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Lists;
import com.listywave.list.application.domain.Reply;
import com.listywave.list.application.dto.response.CommentCreateResponse;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.ListRepository;
import com.listywave.list.repository.ReplyRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public CommentFindResponse getComments(Long listId, int size, Long cursorId) {
        Lists list = listRepository.getById(listId);
        Long totalCount = commentRepository.countByList(list);

        List<Comment> comments = commentRepository.getComments(listId, size, cursorId);
        Map<Comment, List<Reply>> result = comments.stream()
                .collect(toMap(
                        identity(),
                        replyRepository::getAllByComment,
                        (exists, newValue) -> exists,
                        LinkedHashMap::new
                ));
        
        Long cursorIdOfResult = comments.get(comments.size() - 1).getId();
        boolean hasNext = comments.size() >= size;

        return CommentFindResponse.from(totalCount, cursorIdOfResult, hasNext, result);
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
