package com.listywave.collection.repository;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.collection.application.domain.Folder;
import com.listywave.collection.repository.custom.CustomFolderRepository;
import com.listywave.common.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long>, CustomFolderRepository {

    boolean existsByNameValueAndUserId(String nameValue, Long loginUserId);

    default Folder getById(Long folderId) {
        return findById(folderId).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
    }
}
