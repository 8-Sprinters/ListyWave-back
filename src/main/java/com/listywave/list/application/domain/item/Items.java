package com.listywave.list.application.domain.item;

import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.list.ListEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        for (Item item : values) {
            item.updateList(list);
        }
        return new Items(values);
    }

    public List<Item> getValues() {
        return new ArrayList<>(values);
    }

//    public Items sortByRankTop3() {
//        List<Item> sorted = values.stream().sorted(Comparator.comparing(Item::getRanking))
//                .toList();
//        return new Items(sorted);
//    }

}
