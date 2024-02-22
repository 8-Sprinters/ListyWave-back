package com.listywave.history.application.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class HistoryItem {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "history_id")
    private History history;

    @Column(name = "ranking", nullable = false, updatable = false)
    private int rank;

    @Embedded
    private HistoryItemTitle title;

    public HistoryItem(int rank, HistoryItemTitle title) {
        this(null, null, rank, title);
    }

    public void updateHistory(History history) {
        this.history = history;
    }
}
