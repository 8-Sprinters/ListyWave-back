package com.listywave.list.application.service;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.comment.CommentContent;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.list.application.dto.response.CommentCreateResponse;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.repository.comment.CommentRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CommentCreateResponse create(Long listId, String content, Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        ListEntity list = listRepository.getById(listId);

        Comment comment = Comment.create(list, user, new CommentContent(content));
        Comment saved = commentRepository.save(comment);

        applicationEventPublisher.publishEvent(AlarmEvent.comment(list, saved));
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

    public void delete(Long listId, Long commentId, Long loginUserId) {
        listRepository.getById(listId);
        User user = userRepository.getById(loginUserId);
        Comment comment = commentRepository.getById(commentId);

        if (!comment.isOwner(user)) {
            throw new CustomException(INVALID_ACCESS, "댓글은 작성자만 지울 수 있습니다.");
        }

        if (replyRepository.existsByComment(comment)) {
            comment.softDelete();
            return;
        }
        commentRepository.delete(comment);
    }

    public void update(Long listId, Long commentId, Long loginUserId, String content) {
        listRepository.getById(listId);
        User user = userRepository.getById(loginUserId);
        Comment comment = commentRepository.getById(commentId);

        if (!comment.isOwner(user)) {
            throw new CustomException(INVALID_ACCESS, "댓글은 작성자만 수정할 수 있습니다.");
        }
        comment.update(new CommentContent(content));
    }
}
