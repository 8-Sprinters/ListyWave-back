package com.listywave.list.presentation.dto.request;

import java.util.List;

public record ListsDeleteRequest(
        List<Long> listId
) {
}
