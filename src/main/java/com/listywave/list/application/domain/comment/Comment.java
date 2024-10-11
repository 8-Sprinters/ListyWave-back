package com.listywave.list.application.domain.comment;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.mention.Mention;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
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

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "comment")
    private final List<Mention> mentions = new ArrayList<>();

    @Column(nullable = false, length = 5)
    private boolean isDeleted;

    public Comment(ListEntity list, User user, CommentContent content, List<Mention> mentions) {
        this.list = list;
        this.user = user;
        this.commentContent = content;
        mentions.forEach(it -> {
            this.mentions.add(it);
            it.setComment(this);
        });
        this.isDeleted = false;
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
