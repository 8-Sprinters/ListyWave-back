package com.listywave.list.application.domain.item;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Item extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "list_id")
    private ListEntity list;

    @Column(nullable = false)
    private int ranking;

    @Embedded
    private ItemTitle title;

    @Embedded
    private ItemComment comment;

    @Embedded
    private ItemLink link;

    @Embedded
    private ItemImageUrl imageUrl;

    private String imageKey;

    public static Item init(int ranking, ItemTitle title, ItemComment comment, ItemLink link, ItemImageUrl imageUrl) {
        return new Item(null, ranking, title, comment, link, imageUrl, null);
    }

    public void updateItemImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public void updateItemImageUrl(String imageUrl) {
        this.imageUrl = new ItemImageUrl(imageUrl);
    }

    public void updateList(ListEntity list) {
        this.list = list;
    }
}
