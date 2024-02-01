package com.listywave.list.presentation.dto.response;

public record ListCreateResponse(Long listId){

    public static ListCreateResponse of(Long id) {
        return new ListCreateResponse(
                id
        );
    }
}
