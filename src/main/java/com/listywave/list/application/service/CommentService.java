package com.listywave.list.application.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.ListEntity;
import com.listywave.list.application.domain.Reply;
import com.listywave.list.application.dto.response.CommentCreateResponse;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.repository.CommentRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
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

    public CommentCreateResponse create(Long listId, String content) {
        // TODO: 프론트 단에서 댓글 생성 테스트 끝나면 원래대로 복구
//        Long userId = jwtManager.read(accessToken);
        User user = userRepository.getById(1L);
        ListEntity list = listRepository.getById(listId);

        Comment comment = Comment.create(list, user, content);
        Comment saved = commentRepository.save(comment);

        return CommentCreateResponse.of(saved, user);
    }

    public CommentFindResponse getComments(Long listId, int size, Long cursorId) {
        ListEntity list = listRepository.getById(listId);

        List<Comment> comments = commentRepository.getComments(list, size, cursorId);
        if (comments.isEmpty()) {
            return CommentFindResponse.emptyResponse();
        }

        boolean hasNext = false;
        if (comments.size() > size) {
            hasNext = true;
            comments = comments.subList(0, size);
        }

        Long newCursorId = comments.get(comments.size() - 1).getId();
        Map<Comment, List<Reply>> result = comments.stream()
                .collect(toMap(
                        identity(),
                        replyRepository::getAllByComment,
                        (exists, newValue) -> exists,
                        LinkedHashMap::new
                ));
        Long totalCount = commentRepository.countByList(list) + replyRepository.countByList(list);
        return CommentFindResponse.from(totalCount, newCursorId, hasNext, result);
    }

    public void delete(Long listId, Long commentId) {
        // TODO: 프론트 단에서 댓글 생성 테스트 끝나면 원래대로 복구
//        Long userId = jwtManager.read(accessToken);
//        userRepository.getById(userId);
        listRepository.getById(listId);

        Comment comment = commentRepository.getById(commentId);
        if (replyRepository.existsByComment(comment)) {
            comment.softDelete();
            return;
        }
        commentRepository.delete(comment);
    }
}
