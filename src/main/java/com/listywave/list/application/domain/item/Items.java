package com.listywave.list.application.domain.item;

import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.exception.CustomException;
import com.listywave.history.application.domain.HistoryItem;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED, force = true)
public class Items {

    private static final int MIN_SIZE = 3;
    private static final int MAX_SIZE = 10;

    @OneToMany(fetch = LAZY, mappedBy = "list", cascade = ALL, orphanRemoval = true)
    private final List<Item> values;

    public Items(List<Item> items) {
        validateSize(items);
        this.values = new ArrayList<>(items);
    }

    private void validateSize(List<Item> items) {
        if (items.size() < MIN_SIZE || items.size() > MAX_SIZE) {
            throw new CustomException(INVALID_COUNT);
        }
    }

    public Items sortByRank() {
        List<Item> sorted = values.stream()
                .sorted(Comparator.comparing(Item::getRanking))
                .toList();
        return new Items(sorted);
    }

    public String getFirstImageUrl() {
        List<Item> sorted = sortByRank().getValues();
        return sorted.stream()
                .map(Item::getImageUrl)
                .filter(ItemImageUrl::hasValue)
                .map(ItemImageUrl::getValue)
                .findFirst()
                .orElse("");
    }

    public Items updateList(ListEntity list) {
        values.forEach(item -> item.updateList(list));
        return new Items(values);
    }

    public boolean canCreateHistory(Items newItems) {
        if (this.size() != newItems.size()) {
            return true;
        }

        Map<ItemTitle, Integer> before = this.mapTitleAndRank();
        Map<ItemTitle, Integer> updated = newItems.mapTitleAndRank();

        for (var entry : before.entrySet()) {
            ItemTitle beforeTitle = entry.getKey();
            Integer beforeRank = entry.getValue();

            if (!updated.containsKey(beforeTitle)) {
                return true;
            }
            Integer updatedRank = updated.get(beforeTitle);
            if (!beforeRank.equals(updatedRank)) {
                return true;
            }
        }
        return false;
    }

    private Map<ItemTitle, Integer> mapTitleAndRank() {
        return sortByRank().getValues().stream()
                .collect(toMap(
                        Item::getTitle,
                        Item::getRanking,
                        (item1, item2) -> item2,
                        LinkedHashMap::new
                ));
    }

    public List<HistoryItem> toHistoryItems() {
        return values.stream()
                .map(Item::toHistoryItem)
                .toList();
    }

    public boolean isChange(Items newItems) {
        if (this.size() != newItems.size()) {
            return true;
        }

        for (int i = 0; i < this.size(); i++) {
            Item beforeItem = this.get(i);
            Item newItem = newItems.get(i);

            if (!beforeItem.equals(newItem)) {
                return true;
            }
        }
        return false;
    }

    public void updateAll(Items newItems, ListEntity list) {
        removeDeleteItems(newItems);
        addNewItems(newItems, list);
    }

    private void removeDeleteItems(Items newItems) {
        Set<Item> beforeLabels = new HashSet<>(this.values);
        newItems.values.forEach(beforeLabels::remove);
        this.values.removeAll(beforeLabels);
    }

    private void addNewItems(Items newItems, ListEntity list) {
        Set<Item> newItemsSet = new HashSet<>(newItems.values);
        this.values.forEach(newItemsSet::remove);
        newItemsSet.forEach(newItem -> newItem.updateList(list));
        this.values.addAll(newItemsSet);
    }

    public boolean anyMatchTitle(String keyword) {
        return values.stream()
                .anyMatch(item -> item.isMatchTitle(keyword));
    }

    public boolean anyMatchComment(String keyword) {
        return values.stream()
                .anyMatch(item -> item.isMatchComment(keyword));
    }

    public boolean contains(Item item) {
        return this.values.contains(item);
    }

    public Item get(int index) {
        return values.get(index);
    }

    public int size() {
        return values.size();
    }

    public List<Item> getValues() {
        return new ArrayList<>(values);
    }

    public String getRepresentImageUrl() {
        return sortByRank().values.stream()
                .map(Item::getImageUrl)
                .filter(ItemImageUrl::hasValue)
                .map(ItemImageUrl::getValue)
                .findFirst()
                .orElse("");
    }
}
