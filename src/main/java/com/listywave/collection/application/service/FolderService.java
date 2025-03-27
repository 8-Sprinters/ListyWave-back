package com.listywave.collection.application.service;

import static com.listywave.common.exception.ErrorCode.DUPLICATE_FOLDER_NAME_EXCEPTION;

import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.application.domain.Folder;
import com.listywave.collection.application.domain.FolderName;
import com.listywave.collection.application.dto.FindFolderResponse;
import com.listywave.collection.application.dto.FolderCreateResponse;
import com.listywave.collection.repository.CollectionRepository;
import com.listywave.collection.repository.FolderRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FolderService {

    private final UserService userService;
    private final FolderRepository folderRepository;
    private final CollectionRepository collectionRepository;

    public FolderCreateResponse create(Long userId, String folderName) {
        User user = userService.getById(userId);
        if (folderRepository.existsByNameValueAndUserId(folderName, user.getId())) {
            throw new CustomException(DUPLICATE_FOLDER_NAME_EXCEPTION);
        }
        Folder folder = new Folder(user.getId(), new FolderName(folderName));
        folder = folderRepository.save(folder);
        return new FolderCreateResponse(folder.getId());
    }

    public void updateFolder(Long loginUserId, Long folderId, String folderName) {
        User user = userService.getById(loginUserId);
        if (folderRepository.existsByNameValueAndUserId(folderName, user.getId())) {
            throw new CustomException(DUPLICATE_FOLDER_NAME_EXCEPTION);
        }
        Folder folder = folderRepository.getById(folderId);
        folder.updateFolderName(user.getId(), new FolderName(folderName));
    }

    public void deleteFolder(Long loginUserId, Long folderId) {
        User user = userService.getById(loginUserId);
        Folder folder = folderRepository.getById(folderId);
        folder.validateOwner(user.getId());
        cancelCollectionsIn(folder);
        folderRepository.deleteById(folderId);
    }

    private void cancelCollectionsIn(Folder folder) {
        List<Collect> collects = collectionRepository.findAllByFolder(folder);
        collects.forEach(collect -> collect.getList().decreaseCollectCount());
        collectionRepository.deleteAllByFolder(folder);
    }

    public FindFolderResponse getFolders(Long loginUserId) {
        User user = userService.getById(loginUserId);
        return FindFolderResponse.of(folderRepository.findByFolders(user.getId()));
    }
}
