package com.listywave.user.repository.user.custom;

import com.listywave.list.application.domain.CategoryType;
import com.listywave.list.application.domain.Lists;
import com.listywave.user.application.domain.User;
import java.util.List;

public interface CustomUserRepository {

    List<Lists> findFeedLists(Long userId, String type, CategoryType category, Long cursorId, int size);

    List<User> getRecommendUsers();
}
