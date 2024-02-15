package com.listywave.list.application.domain.item;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.presentation.dto.request.ItemCreateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
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

    public static Item createItem(ItemCreateRequest items, ListEntity list) {
        return Item.builder()
                .list(list)
                .ranking(items.rank())
                .title(
                        ItemTitle.builder()
                                .value(items.title())
                                .build()
                )
                .comment(
                        ItemComment.builder()
                                .value(items.comment())
                                .build()
                )
                .link(
                        ItemLink.builder()
                                .value(items.link())
                                .build()
                )
                .imageUrl(
                        ItemImageUrl.builder()
                                .value(items.imageUrl())
                                .build()
                )
                .build();
    }

    public void updateItemImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public void updateItemImageUrl(String imageUrl) {
        this.imageUrl = ItemImageUrl.builder()
                .value(imageUrl)
                .build();
    }

    public String getTitle() {
        return title.getValue();
    }

    public String getComment() {
        return comment.getValue();
    }

    public String getLink() {
        return link.getValue();
    }

    public String getImageUrl() {
        return imageUrl.getValue();
    }
}
