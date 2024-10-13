package com.listywave.collection.application.dto;

import java.util.List;

public record FindFolderResponse(
        List<FolderResponse> folders
) {

    public static FindFolderResponse of(List<FolderResponse> list){
        return new FindFolderResponse(list);
    }
}
