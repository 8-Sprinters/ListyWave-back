package com.listywave.list.application.domain.reaction;

import com.listywave.common.BaseEntity;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

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

    public void updateCount(int changeCount) {
        this.count += changeCount;
    }
}
