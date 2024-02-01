package com.listywave.list.presentation.dto.request;

public record ItemCreateRequest(
        int rank,
        String title,
        String comment,
        String link,
        String imageUrl
) {

}
