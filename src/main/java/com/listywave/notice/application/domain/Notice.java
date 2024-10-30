package com.listywave.notice.application.domain;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Notice extends BaseEntity {

    @Column(name = "code", nullable = false)
    private NoticeType type;

    @Embedded
    private NoticeTitle title;

    @Embedded
    private NoticeDescription description;

    @OneToMany(mappedBy = "notice", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private final List<NoticeContent> contents = new ArrayList<>();

    public Notice(NoticeType type, NoticeTitle title, NoticeDescription description) {
        this.type = type;
        this.title = title;
        this.description = description;
    }

    public void addContents(List<NoticeContent> contents) {
        this.contents.addAll(contents);
    }
}
