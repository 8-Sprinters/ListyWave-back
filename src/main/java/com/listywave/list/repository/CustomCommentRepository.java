package com.listywave.list.repository;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.List;

public interface CustomCommentRepository {

    List<Comment> getComments(ListEntity list, int size, Long cursorId);

    Long countByList(ListEntity list);
}
