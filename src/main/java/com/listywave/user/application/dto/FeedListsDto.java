package com.listywave.user.application.dto;

import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Lists;
import java.util.List;
import java.util.stream.Collectors;

public record FeedListsDto(
        Long id,
        String title,
        Boolean isPublic,
        List<ListItemsDto> listItems
) {
    public static FeedListsDto of(Lists lists) {
        return new FeedListsDto(
                lists.getId(),
                lists.getTitle(),
                lists.isPublic(),
                lists.getItems().stream()
                        .map(ListItemsDto::of)
                        .collect(Collectors.toList())
        );
    }
}

record ListItemsDto(
        Long id,
        int ranking,
        String title,
        String imageUrl
) {
    public static ListItemsDto of(Item item) {
        return new ListItemsDto(
                item.getId(),
                item.getRanking(),
                item.getTitle(),
                item.getImageUrl()
        );
    }

}
