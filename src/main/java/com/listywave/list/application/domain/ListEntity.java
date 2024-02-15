package com.listywave.list.application.domain;

import static com.listywave.common.util.StringUtils.match;

import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.application.vo.ItemComment;
import com.listywave.list.application.vo.ItemImageUrl;
import com.listywave.list.application.vo.ItemLink;
import com.listywave.list.application.vo.ItemTitle;
import com.listywave.list.application.vo.LabelName;
import com.listywave.list.application.vo.ListDescription;
import com.listywave.list.application.vo.ListTitle;
import com.listywave.list.presentation.dto.request.ItemCreateRequest;
import com.listywave.user.application.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "list")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Convert(converter = CategoryTypeConverter.class)
    private CategoryType category;

    @Builder.Default
    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Label> labels = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    public static void addLabelToList(ListEntity list, List<String> labels) {
        for (String label : labels) {
            list.getLabels().add(
                    Label.builder()
                            .list(list)
                            .labelName(
                                    LabelName.builder()
                                            .value(label)
                                            .build()
                            )
                            .build()
            );
        }
    }

    private static void addItemToList(ListEntity list, List<ItemCreateRequest> items) {
        for (ItemCreateRequest item : items) {
            list.getItems().add(
                    Item.builder()
                            .list(list)
                            .ranking(item.rank())
                            .title(
                                    ItemTitle.builder()
                                            .value(item.title())
                                            .build()
                            )
                            .comment(
                                    ItemComment.builder()
                                            .value(item.comment())
                                            .build()
                            )
                            .link(
                                    ItemLink.builder()
                                            .value(item.link())
                                            .build()
                            )
                            .imageUrl(
                                    ItemImageUrl.builder()
                                            .value(item.imageUrl())
                                            .build()
                            )
                            .build()
            );
        }
    }

    public static ListEntity createList(
            User user,
            ListCreateCommand command,
            List<String> labels,
            List<ItemCreateRequest> items,
            Boolean isLabels,
            Boolean hasCollaboratorId
    ) {
        ListEntity list = ListEntity.builder()
                .user(user)
                .category(command.category())
                .hasCollaboration(hasCollaboratorId)
                .title(command.title())
                .isPublic(command.isPublic())
                .backgroundColor(command.backgroundColor())
                .description(command.description())
                .build();

        if (isLabels) {
            addLabelToList(list, labels);
        }
        addItemToList(list, items);
        return list;
    }

    public boolean isRelatedWith(String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        if (match(title.getValue(), keyword)) {
            return true;
        }
        if (labels.stream().anyMatch(label -> match(label.getLabelName(), keyword))) {
            return true;
        }
        if (items.stream().anyMatch(item -> match(item.getTitle(), keyword) || match(item.getComment(), keyword))) {
            return true;
        }
        return false;
    }

    public boolean isIncluded(CategoryType category) {
        if (category.equals(CategoryType.ENTIRE)) {
            return true;
        }
        if (this.category.equals(category)) {
            return true;
        }
        return false;
    }

    public void sortItems() {
        this.getItems().sort(Comparator.comparing(Item::getRanking));
    }

    public boolean canDeleteBy(User user) {
        return this.user.equals(user);
    }

    public void incrementCollectCount() {
        this.collectCount++;
    }

    public void decrementCollectCount() {
        this.collectCount--;
    }

    public String getCategoryName() {
        return category.name();
    }

    public String getTitle() {
        return title.getValue();
    }

    public String getDescription() {
        return description.getValue();
    }
}
