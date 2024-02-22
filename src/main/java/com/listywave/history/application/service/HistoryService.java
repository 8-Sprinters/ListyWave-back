package com.listywave.history.application.service;

import com.listywave.history.application.domain.History;
import com.listywave.history.application.dto.HistorySearchResponse;
import com.listywave.history.repository.HistoryRepository;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final UserRepository userRepository;
    private final ListRepository listRepository;
    private final HistoryRepository historyRepository;

    public List<HistorySearchResponse> searchHistories(Long listId) {
        ListEntity list = listRepository.getById(listId);
        List<History> histories = historyRepository.findAllByList(list);

        return HistorySearchResponse.toList(histories);
    }

    public void updatePublic(Long historyId, Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        History history = historyRepository.getReferenceById(historyId);

        history.validateOwner(user);
        history.updatePublic();
    }

    public void deleteHistory(Long historyId, Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        History history = historyRepository.getReferenceById(historyId);

        history.validateOwner(user);
        historyRepository.delete(history);
    }
}
