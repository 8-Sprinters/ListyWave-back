package com.listywave.list.repository;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.ListEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    default Comment getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
    }

    List<Comment> findAllByList(ListEntity list);

    Long countByList(ListEntity list);
}
