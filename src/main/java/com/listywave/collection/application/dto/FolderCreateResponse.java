package com.listywave.collection.application.dto;

public record FolderCreateResponse(Long folderId) {

    public static FolderCreateResponse of(Long id) {
        return new FolderCreateResponse(id);
    }
}
