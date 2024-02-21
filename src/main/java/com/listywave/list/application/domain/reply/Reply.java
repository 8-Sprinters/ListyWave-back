package com.listywave.list.application.domain.reply;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.comment.CommentContent;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    public boolean isOwner(User user) {
        return this.user.equals(user);
    }

    public void update(CommentContent content) {
        this.commentContent = content;
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
