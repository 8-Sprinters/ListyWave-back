package com.listywave.list.application.domain;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.vo.Content;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @JoinColumn(name = "list_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ListEntity list;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Embedded
    private Content content;

    @Column(nullable = false, length = 5)
    private Boolean isDeleted;

    public static Comment create(ListEntity list, User user, String content) {
        return new Comment(list, user, new Content(content), false);
    }

    public boolean canDeleteBy(User user) {
        return this.user.equals(user);
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public String getContent() {
        return content.getValue();
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
}
