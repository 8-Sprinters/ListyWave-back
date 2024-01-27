package com.listywave.list.domain;

import com.listywave.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Column(nullable = false)
    private int rank;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false, length = 2048)
    private String link;

    @Column(nullable = false, length = 2048)
    private String imageUrl;
}
