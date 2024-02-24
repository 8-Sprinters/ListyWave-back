package com.listywave.list.repository.reply;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long>, CustomReplyRepository {

    default Reply getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "존재하지 않는 답글입니다."));
    }

    boolean existsByComment(Comment comment);

    List<Reply> getAllByComment(Comment comment);

    @Modifying
    @Query("delete from Reply r where r.comment in :comments")
    void deleteAllByCommentIn(@Param("comments") List<Comment> comments);
}
