package com.listywave.list.application.domain.reaction;

import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.TemporalType.TIMESTAMP;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_reaction",
        uniqueConstraints= {
                @UniqueConstraint(
                        name = "UniqueIdAndUserIdAndReaction",
                        columnNames = {"id", "user_id", "reaction"}
                )
        }
)
public class UserReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ListEntity list;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(updatable = false)
    private LocalDateTime createdDate;

    public static UserReaction create(Long userId, ListEntity list, Reaction reaction) {
        return UserReaction.builder()
                .userId(userId)
                .list(list)
                .reaction(reaction)
                .build();
    }
}
