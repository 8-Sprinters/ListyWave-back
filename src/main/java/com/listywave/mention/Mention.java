package com.listywave.mention;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Mention {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mentioned_user_id")
    private User user;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id", nullable = true, foreignKey = @ForeignKey(name = "mention_comment_fk"))
    private Comment comment;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reply_id", nullable = true, foreignKey = @ForeignKey(name = "mention_reply_fk"))
    private Reply reply;

    public Mention(User user) {
        this.user = user;
    }
}
