package com.listywave.list.application.domain.list;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.reverseOrder;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.category.CategoryType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;

public record ListEntities(
        List<ListEntity> listEntities
) {

    public ListEntities(List<ListEntity> listEntities) {
        this.listEntities = new ArrayList<>(listEntities);
    }

    public ListEntities filterBy(CategoryType categoryType) {
        List<ListEntity> filtered = listEntities.stream()
                .filter(list -> list.isCategoryType(categoryType))
                .toList();
        return new ListEntities(filtered);
    }

    public ListEntities filterBy(String keyword) {
        List<ListEntity> filtered = listEntities.stream()
                .filter(list -> list.isMatch(keyword))
                .toList();
        return new ListEntities(filtered);
    }

    public ListEntities sortBy(SortType sortType, String keyword) {
        List<ListEntity> result = switch (sortType) {
            case NEW -> listEntities.stream()
                    .sorted(comparing(ListEntity::getUpdatedDate, reverseOrder()))
                    .toList();

            case OLD -> listEntities.stream()
                    .sorted(comparing(ListEntity::getUpdatedDate))
                    .toList();

            case COLLECTED -> listEntities.stream()
                    .sorted(comparing(ListEntity::getCollectCount))
                    .toList();

            case RELATED -> listEntities.stream()
                    .sorted(comparingInt(list -> list.scoreRelation(keyword)))
                    .toList();
        };

        return new ListEntities(result);
    }

    public ListEntities paging(@Nullable ListEntity cursorList, int limit) {
        if (cursorList != null) {
            int newCursorIndex = listEntities.indexOf(cursorList);
            if (newCursorIndex == -1) {
                throw new CustomException(RESOURCE_NOT_FOUND);
            }
            newCursorIndex += 1;

            if (newCursorIndex + limit <= listEntities.size()) {
                return new ListEntities(listEntities.subList(newCursorIndex, newCursorIndex + limit));
            }
            return new ListEntities(listEntities.subList(newCursorIndex, listEntities.size()));
        }

        if (listEntities.size() < limit) {
            return new ListEntities(listEntities);
        }
        return new ListEntities(listEntities.subList(0, limit));
    }

    public int size() {
        return listEntities.size();
    }

    @Override
    public List<ListEntity> listEntities() {
        return new ArrayList<>(listEntities);
    }
}
