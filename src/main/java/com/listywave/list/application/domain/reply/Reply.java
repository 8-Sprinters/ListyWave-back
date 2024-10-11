package com.listywave.list.application.domain.reply;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.comment.CommentContent;
import com.listywave.mention.Mention;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Reply extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private CommentContent commentContent;

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "reply")
    private final List<Mention> mentions = new ArrayList<>();

    public Reply(Comment comment, User user, CommentContent commentContent, List<Mention> mentions) {
        this.comment = comment;
        this.user = user;
        this.commentContent = commentContent;
        mentions.forEach(it -> {
            this.mentions.add(it);
            it.setReply(this);
        });
    }

    public boolean isOwner(User user) {
        return this.user.equals(user);
    }

    public void update(CommentContent content, List<Mention> mentions) {
        this.commentContent = content;
        updateMentions(mentions);
    }

    private void updateMentions(List<Mention> mentions) {
        Set<Mention> removable = new LinkedHashSet<>(this.mentions);
        mentions.forEach(removable::remove);
        this.mentions.removeAll(removable);

        Set<Mention> addable = new LinkedHashSet<>(mentions);
        this.mentions.forEach(addable::remove);
        this.mentions.addAll(addable);
    }

    public Long getCommentId() {
        return comment.getId();
    }

    public String getCommentContent() {
        return commentContent.getValue();
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getUserNickname() {
        return user.getNickname();
    }

    public String getUserProfileImageUrl() {
        return user.getProfileImageUrl();
    }
}
