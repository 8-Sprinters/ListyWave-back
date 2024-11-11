package com.listywave.notice.application.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class NoticeContent {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(name = "orders", nullable = false)
    private int order;

    @Column(nullable = false, length = 30)
    private ContentType type;

    @Column(nullable = true, length = 1000)
    private String description;

    @Column(nullable = true, length = 2048)
    private String imageUrl;

    @Column(nullable = true, length = 50)
    private String buttonName;

    @Column(nullable = true, length = 2048)
    private String buttonLink;

    public static NoticeContent create(
            Notice notice,
            int order,
            ContentType type,
            @Nullable String description,
            @Nullable String imageUrl,
            @Nullable String buttonName,
            @Nullable String buttonLink
    ) {
        return new NoticeContent(null, notice, order, type, description, imageUrl, buttonName, buttonLink);
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
