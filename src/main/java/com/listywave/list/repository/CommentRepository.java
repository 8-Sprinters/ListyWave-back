package com.listywave.list.repository;

import static com.listywave.common.exception.ErrorCode.NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Lists;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    default Comment getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(NOT_FOUND));
    }

    List<Comment> findAllByList(Lists list);

    Long countByList(Lists list);
}
