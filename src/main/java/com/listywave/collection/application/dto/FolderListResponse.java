package com.listywave.collection.application.dto;

import java.util.List;

public record FolderListResponse(
        List<FolderResponse> folders
) {

    public static FolderListResponse of(List<FolderResponse> list){
        return new FolderListResponse(list);
    }
}
