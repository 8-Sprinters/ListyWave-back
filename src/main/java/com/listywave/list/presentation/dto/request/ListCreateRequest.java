package com.listywave.list.presentation.dto.request;

import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.domain.Item;
import java.util.List;

public record ListCreateRequest(
        Long ownerId,
        String category,
        String labels,
        Boolean hasCollaboration,
        List<Long> collaboratorIds,
        String title,
        String description,
        Boolean isPublic,
        String backgroundColor,
        List<ItemCreateRequest> items
) {
    public ListCreateCommand of(ListCreateRequest listCreateRequest){
        return new ListCreateCommand(
                listCreateRequest.ownerId(),
                listCreateRequest.category(),
                listCreateRequest.labels(),
                listCreateRequest.hasCollaboration(),
                listCreateRequest.title(),
                listCreateRequest.description(),
                listCreateRequest.isPublic(),
                listCreateRequest.backgroundColor()
           );
    }
}
