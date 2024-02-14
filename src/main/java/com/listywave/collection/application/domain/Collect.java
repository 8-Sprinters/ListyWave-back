package com.listywave.collection.application.domain;

import com.listywave.list.application.domain.ListEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "collection")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Collect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "list_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ListEntity list;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public static Collect createCollection(ListEntity list, Long userId) {
        return Collect.builder()
                .list(list)
                .userId(userId)
                .build();
    }
}
