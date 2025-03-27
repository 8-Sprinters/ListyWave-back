package com.listywave.collection.application.service;

import com.listywave.alarm.application.domain.AlarmCreateEvent;
import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.application.domain.Folder;
import com.listywave.collection.application.dto.CollectionFindResponse;
import com.listywave.collection.repository.CollectionRepository;
import com.listywave.collection.repository.FolderRepository;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.dto.response.CategoryTypeResponse;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectionService {

    private final UserRepository userRepository;
    private final ListRepository listRepository;
    private final FolderRepository folderRepository;
    private final CollectionRepository collectionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final static String FOLDER_ENTIRE_NAME = "전체";

    public void collectOrCancel(Long listId, Long folderId, Long userId) {
        User user = userRepository.getById(userId);
        ListEntity list = listRepository.getById(listId);
        Folder folder = folderRepository.getById(folderId);

        folder.validateOwner(userId);
        list.validateNotOwner(user);

        if (collectionRepository.existsByListAndUserId(list, user.getId())) {
            cancelCollect(list, user.getId());
        } else {
            addCollect(list, user, folder);
        }
    }

    private void cancelCollect(ListEntity list, Long userId) {
        collectionRepository.deleteByListAndUserId(list, userId);
        list.decreaseCollectCount();
    }

    private void addCollect(ListEntity list, User user, Folder folder) {
        Collect collection = new Collect(list, user.getId(), folder);
        collectionRepository.save(collection);
        list.increaseCollectCount();

        applicationEventPublisher.publishEvent(AlarmCreateEvent.collect(user, list));
    }

    public CollectionFindResponse getCollection(Long userId, Long cursorId, Pageable pageable, Long folderId) {
        User user = userRepository.getById(userId);
        String folderName = FOLDER_ENTIRE_NAME;
        if (folderId != 0L) {
            Folder folder = folderRepository.getById(folderId);
            folderName = folder.getFolderName();
        }
        Slice<Collect> result = collectionRepository.getAllCollectionList(cursorId, pageable, user.getId(), folderId);
        List<Collect> collectionList = result.getContent();

        cursorId = null;
        if (!collectionList.isEmpty()) {
            cursorId = collectionList.get(collectionList.size() - 1).getId();
        }
        return CollectionFindResponse.of(cursorId, result.hasNext(), collectionList, folderName);
    }

    public List<CategoryTypeResponse> getCategoriesOfCollection(Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        List<CategoryType> categories = collectionRepository.getCategoriesByCollect(user);
        categories.add(CategoryType.ENTIRE);
        List<CategoryType> list = categories.stream().sorted(Comparator.comparing(CategoryType::getCode)).toList();
        return list.stream()
                .map(CategoryTypeResponse::of)
                .toList();
    }
}
