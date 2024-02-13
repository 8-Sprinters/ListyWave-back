package com.listywave.list.repository;

import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.ListEntity;
import java.util.List;

public interface CustomCommentRepository {

    List<Comment> getComments(ListEntity list, int size, Long cursorId);
}
