package com.listywave.list.repository.comment;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    default Comment getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
    }

    List<Comment> findAllByList(ListEntity list);

    @Query("select c from Comment c where c.list in :lists")
    List<Comment> findAllByListIn(@Param("lists") List<ListEntity> lists);

    long countCommentsByList(ListEntity list);

    @Nullable
    Comment findFirstByListOrderByCreatedDateDesc(ListEntity list);
}
