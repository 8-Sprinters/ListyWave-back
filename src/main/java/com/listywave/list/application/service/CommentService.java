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
import com.listywave.mention.Mention;
import com.listywave.mention.MentionRepository;
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
    private final MentionRepository mentionRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CommentCreateResponse create(Long listId, Long writerId, String content, List<Long> mentionIds) {
        User writer = userRepository.getById(writerId);
        ListEntity list = listRepository.getById(listId);
        List<Mention> mentions = toMentions(mentionIds);

        Comment comment = commentRepository.save(new Comment(list, writer, new CommentContent(content), mentions));

        applicationEventPublisher.publishEvent(AlarmEvent.comment(list, comment));
        return CommentCreateResponse.of(comment, writer);
    }

    private List<Mention> toMentions(List<Long> mentionIds) {
        return mentionIds.stream()
                .map(userRepository::getById)
                .map(Mention::new)
                .toList();
    }

    public CommentFindResponse findCommentBy(Long listId, int size, Long cursorId) {
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

    public void delete(Long listId, Long commentId, Long userId) {
        listRepository.getById(listId);
        User user = userRepository.getById(userId);
        Comment comment = commentRepository.getById(commentId);

        if (!comment.isOwner(user)) {
            throw new CustomException(INVALID_ACCESS, "댓글은 작성자만 지울 수 있습니다.");
        }

        if (replyRepository.existsByComment(comment)) {
            comment.softDelete();
            return;
        }
        commentRepository.delete(comment);
        mentionRepository.deleteAllByComment(comment);
    }

    public void update(Long listId, Long writerId, Long commentId, String content, List<Long> mentionIds) {
        listRepository.getById(listId);
        User writer = userRepository.getById(writerId);
        Comment comment = commentRepository.getById(commentId);

        if (!comment.isOwner(writer)) {
            throw new CustomException(INVALID_ACCESS, "댓글은 작성자만 수정할 수 있습니다.");
        }

        List<Mention> mentions = toMentions(mentionIds);
        comment.update(new CommentContent(content), mentions);
    }
}
