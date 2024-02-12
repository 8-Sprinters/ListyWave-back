package com.listywave.list.repository.reply;

import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>, CustomReplyRepository {

    boolean existsByComment(Comment comment);

    List<Reply> getAllByComment(Comment comment);

    void deleteAllByCommentIn(List<Comment> comments);
}
