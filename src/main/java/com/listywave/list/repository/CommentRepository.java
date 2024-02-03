package com.listywave.list.repository;

import static com.listywave.common.exception.ErrorCode.NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    default Comment getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(NOT_FOUND));
    }
}
