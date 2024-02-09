package com.listywave.user.repository.user.custom;

import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.Lists;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.CollaboratorResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomUserRepository {

    List<Lists> findFeedLists(Long userId, String type, CategoryType category, Long cursorId, int size);

    List<User> getRecommendUsers();

    Long getCollaboratorCount(String search, User user);

    Slice<CollaboratorResponse> getCollaborators(String search, Pageable pageable, User user);
}
