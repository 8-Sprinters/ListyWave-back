package com.listywave.list.repository;

import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Lists;
import java.util.List;

public interface CustomCommentRepository {

    List<Comment> getComments(Lists list, int size, Long cursorId);
}
