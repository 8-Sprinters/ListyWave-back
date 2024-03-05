package com.listywave.list.application.domain.list;

import static com.listywave.common.exception.ErrorCode.DELETED_USER_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static com.listywave.list.application.domain.category.CategoryType.ENTIRE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.TemporalType.TIMESTAMP;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.collaborator.application.domain.Collaborators;
import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.category.CategoryTypeConverter;
import com.listywave.list.application.domain.item.Item;
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

    public Items getSortedItems() {
        return items.sortByRank();
    }

    public Items getTop3Items() {
        return items.getTop3();
    }

    public String getFirstItemImageUrl() {
        return items.getFirstImageUrl();
    }

    public void validateOwner(User user) {
        if (!this.user.equals(user)) {
            throw new CustomException(INVALID_ACCESS);
        }
    }

    public void validateNotOwner(User user) {
        if (this.user.equals(user)) {
            throw new CustomException(INVALID_ACCESS);
        }
    }

    public boolean isCategoryType(CategoryType category) {
        return category.equals(ENTIRE) || this.category.equals(category);
    }

    public boolean isMatch(String keyword) {
        return keyword.isBlank() ||
                title.isMatch(keyword) ||
                description.isMatch(keyword) ||
                labels.anyMatch(keyword) ||
                items.anyMatchTitle(keyword) ||
                items.anyMatchComment(keyword);
    }

    public int scoreRelation(String keyword) {
        int totalScore = 0;
        if (title.isMatch(keyword)) {
            totalScore += 5;
        }
        if (description.isMatch(keyword)) {
            totalScore += 4;
        }
        if (labels.anyMatch(keyword)) {
            totalScore += 3;
        }
        if (items.anyMatchTitle(keyword)) {
            totalScore += 2;
        }
        if (items.anyMatchComment(keyword)) {
            totalScore += 1;
        }
        return totalScore;
    }

    public void increaseCollectCount() {
        this.collectCount++;
    }

    public void decreaseCollectCount() {
        this.collectCount--;
    }

    public boolean canCreateHistory(Items newItems) {
        return this.items.canCreateHistory(newItems);
    }

    public void update(
            User owner,
            CategoryType category,
            ListTitle title,
            ListDescription description,
            boolean isPublic,
            String backgroundColor,
            boolean hasCollaboration,
            LocalDateTime updatedDate,
            Labels newLabels,
            Items newItems
    ) {
        validateOwner(owner);

        this.category = category;
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
        this.backgroundColor = backgroundColor;
        this.hasCollaboration = hasCollaboration;
        this.updatedDate = updatedDate;

        if (this.labels.isChange(newLabels)) {
            this.labels.updateAll(newLabels, this);
        }
        if (this.items.isChange(newItems)) {
            this.items.updateAll(newItems, this);
        }
    }

    public void validateHasItem(Item item) {
        if (!this.items.contains(item)) {
            throw new CustomException(RESOURCE_NOT_FOUND);
        }
    }

    public void updateVisibility(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getRepresentImageUrl() {
        return items.getRepresentImageUrl();
    }

    public boolean isDeletedUser() {
        return user.getIsDelete();
    }

    public void validateOwnerIsNotDelete() {
        if (this.user.getIsDelete()) {
            throw new CustomException(DELETED_USER_EXCEPTION, "탈퇴한 회원의 리스트입니다.");
        }
    }

    public void validateUpdateAuthority(User loginUser, Collaborators beforeCollaborators) {
        if (this.user.equals(loginUser)) {
            return;
        }
        if (beforeCollaborators.isEmpty()) {
            return;
        }
        if (beforeCollaborators.contains(loginUser)) {
            return;
        }
        throw new CustomException(INVALID_ACCESS);
    }
}
