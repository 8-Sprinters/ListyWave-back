package com.listywave.list.repository;

import com.listywave.list.application.domain.Comment;
import com.listywave.list.application.domain.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    boolean existsByComment(Comment comment);

    List<Reply> getAllByComment(Comment comment);
}
