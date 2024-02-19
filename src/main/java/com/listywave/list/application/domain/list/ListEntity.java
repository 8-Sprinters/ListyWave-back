package com.listywave.list.application.domain.list;

import static com.listywave.list.application.domain.category.CategoryType.ENTIRE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.TemporalType.TIMESTAMP;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.category.CategoryTypeConverter;
import com.listywave.list.application.domain.item.Items;
import com.listywave.list.application.domain.label.Labels;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "list")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ListEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "owner_id")
    private User user;

    @Column(name = "category_code", length = 10, nullable = false)
    @Convert(converter = CategoryTypeConverter.class)
    private CategoryType category;

    @Embedded
    private ListTitle title;

    @Embedded
    private ListDescription description;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private String backgroundColor;

    @Column(nullable = false)
    private boolean hasCollaboration;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int collectCount;

    @Embedded
    private Labels labels;

    @Embedded
    private Items items;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TIMESTAMP)
    private LocalDateTime createdDate;

    @Temporal(TIMESTAMP)
    private LocalDateTime updatedDate;

    public ListEntity(
            User user, CategoryType category, ListTitle title,
            ListDescription description, boolean isPublic,
            String backgroundColor, boolean hasCollaboration,
            Labels labels, Items items
    ) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
        this.backgroundColor = backgroundColor;
        this.hasCollaboration = hasCollaboration;
        this.labels = labels.updateList(this);
        this.items = items.updateList(this);
        this.updatedDate = LocalDateTime.now();
    }

    public void sortItemsByRank() {
        items = items.sortByRank();
    }

    public void sortItemsByRankTop3() {
        items.sortItemsByRankTop3();
    }

    public String getFirstItemImageUrl() {
        return items.getFirstImageUrl();
    }

    public boolean canDeleteBy(User user) {
        return this.user.equals(user);
    }

    public boolean isCategoryType(CategoryType category) {
        return category.equals(ENTIRE) || this.category.equals(category);
    }

    public boolean isMatch(String keyword) {
        return keyword.isBlank() || title.isMatch(keyword) || description.isMatch(keyword) || labels.anyMatch(keyword);
    }

    public int scoreRelation(String keyword) {
        int totalScore = 0;
        if (title.isMatch(keyword)) {
            totalScore += 10;
        }
        if (description.isMatch(keyword)) {
            totalScore += 5;
        }
        if (labels.anyMatch(keyword)) {
            totalScore += 3;
        }
        return totalScore;
    }

    public void incrementCollectCount() {
        this.collectCount++;
    }

    public void decrementCollectCount() {
        this.collectCount--;
    }
}
