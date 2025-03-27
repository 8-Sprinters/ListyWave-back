package com.listywave.mention;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentionRepository extends JpaRepository<Mention, Long> {

    List<Mention> findAllByComment(Comment comment);

    List<Mention> findAllByReply(Reply reply);
}
