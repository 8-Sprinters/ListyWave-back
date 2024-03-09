package com.listywave.list.application.domain.comment;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Column;
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
public class Comment extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "list_id")
    private ListEntity list;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private CommentContent commentContent;

    @Column(nullable = false, length = 5)
    private boolean isDeleted;

    public static Comment create(ListEntity list, User user, CommentContent content) {
        return new Comment(list, user, content, false);
    }

    public boolean isOwner(User user) {
        return this.user.equals(user);
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void update(CommentContent content) {
        this.commentContent = content;
    }

    public boolean isDeleted() {
        return this.isDeleted;
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

    public String getCommentContent() {
        return commentContent.getValue();
    }
}
