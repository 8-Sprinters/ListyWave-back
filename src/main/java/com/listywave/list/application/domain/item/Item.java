package com.listywave.list.application.domain.item;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.history.application.domain.HistoryItem;
import com.listywave.history.application.domain.HistoryItemTitle;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
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

    public HistoryItem toHistoryItem() {
        return new HistoryItem(this.ranking, new HistoryItemTitle(this.title.getValue()));
    }

    public boolean isMatchTitle(String keyword) {
        return this.title.isMatch(keyword);
    }

    public boolean isMatchComment(String keyword) {
        return this.comment.isMatch(keyword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return getRanking() == item.getRanking() && Objects.equals(getTitle(), item.getTitle()) && Objects.equals(getComment(), item.getComment()) && Objects.equals(getLink(), item.getLink()) && Objects.equals(getImageUrl(), item.getImageUrl()) && Objects.equals(getImageKey(), item.getImageKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRanking(), getTitle(), getComment(), getLink(), getImageUrl(), getImageKey());
    }
}
