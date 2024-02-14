package com.listywave.collection.application.service;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.repository.CollectionRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import com.listywave.list.application.domain.ListEntity;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public void collectOrCancel(Long userId, Long listId, String accessToken) {
        Long tokenUserId = jwtManager.read(accessToken);
        validateUserIdInAccessToken(userId, tokenUserId);

        User user = userRepository.getById(tokenUserId);
        ListEntity list = findListById(listId);

        if (isCollected(list, user.getId())) {
            cancelCollect(list, user.getId());
        } else {
            addCollect(list, user.getId());
        }
    }

    private void addCollect(ListEntity list, Long userId) {
        Collect collection = Collect.createCollection(list, userId);
        collectionRepository.save(collection);
        list.incrementCollectCount();
    }

    private void cancelCollect(ListEntity list, Long userId) {
        collectionRepository.deleteByListAndUserId(list, userId);
        list.decrementCollectCount();
    }

    private boolean isCollected(ListEntity list, Long userId) {
        return collectionRepository.existsByListAndUserId(list, userId);
    }

    private void validateUserIdInAccessToken(Long userId, Long tokenUserId) {
        if (!userId.equals(tokenUserId)) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_USER_MISMATCH);
        }
    }

    private ListEntity findListById(Long listId) {
        return listRepository
                .findById(listId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 리스트입니다."));
    }
}
