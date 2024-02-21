package com.listywave.history.application.domain;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.TemporalType.TIMESTAMP;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class History {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "list_id")
    private ListEntity list;

    @OneToMany(mappedBy = "history", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<HistoryItem> items;

    @Temporal(TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private boolean isPublic;

    public History(ListEntity list, List<HistoryItem> historyItems, LocalDateTime updatedDate, boolean isPublic) {
        this.list = list;
        this.items = updateHistoryItems(historyItems);
        this.createdDate = updatedDate;
        this.isPublic = isPublic;
    }

    private List<HistoryItem> updateHistoryItems(List<HistoryItem> historyItems) {
        historyItems.forEach(historyItem -> historyItem.updateHistory(this));
        return historyItems;
    }

    public void validateOwner(User user) {
        list.validateOwner(user);
    }

    public void updatePublic() {
        if (isPublic) {
            isPublic = false;
            return;
        }
        isPublic = true;
    }
}
