package com.listywave.collection.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.application.dto.CollectionResponse;
import com.listywave.collection.repository.CollectionRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CollectionService {

    private final JwtManager jwtManager;
    private final UserRepository userRepository;
    private final ListRepository listRepository;
    private final CollectionRepository collectionRepository;

    public void collectOrCancel(Long listId, String accessToken) {
        Long tokenUserId = jwtManager.read(accessToken);
        User user = userRepository.getById(tokenUserId);
        ListEntity list = listRepository.getById(listId);

        if (user.isSame(list.getUser().getId())) {
            throw new CustomException(ErrorCode.CANNOT_COLLECT_OWN_LIST);
        }

        if (collectionRepository.existsByListAndUserId(list, user.getId())) {
            cancelCollect(list, user.getId());
        } else {
            addCollect(list, user.getId());
        }
    }

    private void addCollect(ListEntity list, Long userId) {
        Collect collection = new Collect(list, userId);
        collectionRepository.save(collection);
        list.incrementCollectCount();
    }

    private void cancelCollect(ListEntity list, Long userId) {
        collectionRepository.deleteByListAndUserId(list, userId);
        list.decrementCollectCount();
    }

    public CollectionResponse getCollection(String accessToken, Long cursorId, Pageable pageable) {
        Long tokenUserId = jwtManager.read(accessToken);
        User user = userRepository.getById(tokenUserId);

        Slice<Collect> result = collectionRepository.getAllCollectionList(cursorId, pageable, user.getId());
        List<Collect> collectionList = result.getContent();

        cursorId = null;
        if (!collectionList.isEmpty()) {
            cursorId = collectionList.get(collectionList.size() - 1).getId();
//            collectionList.forEach(collect -> collect.getList().sortItemsByRankTop3());
        }
        return CollectionResponse.of(cursorId, result.hasNext(), collectionList);
    }
}
