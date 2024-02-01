package com.listywave.list.application.domain;

import com.listywave.list.application.vo.Labels;
import com.listywave.list.application.vo.ListDescription;
import com.listywave.list.application.vo.ListTitle;
import com.listywave.user.application.domain.User;

import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.presentation.dto.request.ItemCreateRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "LIST")
public class Lists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "category_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Category category;

    @Embedded
    private Labels labels;

    @OneToMany(mappedBy = "list")
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

    public static Lists createList(User user, ListCreateCommand list, List<ItemCreateRequest> items){
        return Lists.builder()
                .user(user)
                .category(new Category(1L, CategoryType.BOOK))
                .labels(list.labels())
                .hasCollaboration(list.hasCollabolation())
                .title(list.title())
                .isPublic(list.isPublic())
                .backgroundColor(list.backgroundColor())
                .description(list.description())
                .build();
    }
}
