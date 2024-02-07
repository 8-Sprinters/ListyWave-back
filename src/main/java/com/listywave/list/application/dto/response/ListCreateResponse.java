package com.listywave.list.application.dto.response;

public record ListCreateResponse(Long listId) {

    public static ListCreateResponse of(Long id) {
        return new ListCreateResponse(
                id
        );
    }
}
