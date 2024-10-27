package com.listywave.reaction.application.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "reaction_stats")
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReactionStats extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ListEntity list;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @Column(nullable = false)
    private int count;

    public synchronized void updateCount(int changeCount) {
        this.count += changeCount;
    }
}
