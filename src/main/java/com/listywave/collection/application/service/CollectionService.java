package com.listywave.collection.application.service;

import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.application.dto.CollectionResponse;
import com.listywave.collection.repository.CollectionRepository;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
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
    private final CollectionRepository collectionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void collectOrCancel(Long listId, Long loginUserId) {
        User loginUser = userRepository.getById(loginUserId);
        ListEntity list = listRepository.getById(listId);

        list.validateOwnerRestriction(loginUser);

        if (collectionRepository.existsByListAndUserId(list, loginUser.getId())) {
            cancelCollect(list, loginUser.getId());
        } else {
            addCollect(list, loginUser);
        }
    }

    private void addCollect(ListEntity list, User user) {
        Collect collection = new Collect(list, user.getId());
        collectionRepository.save(collection);
        list.incrementCollectCount();

        applicationEventPublisher.publishEvent(AlarmEvent.collect(user, list));
    }

    private void cancelCollect(ListEntity list, Long userId) {
        collectionRepository.deleteByListAndUserId(list, userId);
        list.decrementCollectCount();
    }

    public CollectionResponse getCollection(Long loginUserId, Long cursorId, Pageable pageable, CategoryType category) {
        User user = userRepository.getById(loginUserId);
        Slice<Collect> result = collectionRepository.getAllCollectionList(cursorId, pageable, user.getId(), category);
        List<Collect> collectionList = result.getContent();

        cursorId = null;
        if (!collectionList.isEmpty()) {
            cursorId = collectionList.get(collectionList.size() - 1).getId();
//            collectionList.forEach(collect -> collect.getList().sortItemsByRankTop3());
        }
        return CollectionResponse.of(cursorId, result.hasNext(), collectionList);
    }
}
