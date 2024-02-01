package com.listywave.list.application.dto;

public record ListCreateCommand(
        Long ownerId,
        String category,
        String labels,
        Boolean hasCollabolation,
        String title,
        String description,
        Boolean isPublic,
        String backgroundColor
) {
}
