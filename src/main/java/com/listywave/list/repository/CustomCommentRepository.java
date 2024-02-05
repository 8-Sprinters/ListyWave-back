package com.listywave.list.repository;

import com.listywave.list.application.domain.Comment;
import java.util.List;

public interface CustomCommentRepository {

    List<Comment> getComments(Long listId, int size, Long cursorId);
}
