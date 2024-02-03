package com.listywave.list.repository;

import com.listywave.list.application.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    boolean existsByCommentId(Long commentId);
}
