package com.listywave.collection.application.dto;

public record FolderResponse(
        Long folderId,
        String folderName,
        Long listCount
) {
}
