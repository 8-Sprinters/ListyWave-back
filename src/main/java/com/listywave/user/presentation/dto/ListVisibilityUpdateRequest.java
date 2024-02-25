package com.listywave.user.presentation.dto;

public record ListVisibilityUpdateRequest(
        Long listId,
        Boolean isPublic
) {
}
