package com.listywave.collection.repository.custom;

import com.listywave.collection.application.dto.FolderResponse;

import java.util.List;

public interface CustomFolderRepository {

    List<FolderResponse> findByFolders(Long loginUserId);
}
