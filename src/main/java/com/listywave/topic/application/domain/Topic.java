package com.listywave.topic.application.domain;

import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListDescription;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Topic extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "topic_user_fk"))
    private User user;

    @Column(name = "category_code", length = 10, nullable = false)
    private CategoryType category;

    @Embedded
    private ListTitle title;

    @Embedded
    private ListDescription description;

    @Column(nullable = false)
    private boolean isAnonymous;

    @Column(nullable = false)
    private boolean isExposed;
}
