package com.listywave.user.presentation.dto;

import java.util.List;

public record UserListsDeleteRequest(
        List<Long> listId
) {
}
