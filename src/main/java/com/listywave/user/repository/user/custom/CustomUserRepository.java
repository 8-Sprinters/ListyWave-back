package com.listywave.user.repository.user.custom;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.search.UserSearchResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomUserRepository {

    Slice<ListEntity> findFeedLists(
            List<Collaborator> listBycollabo, Long userId, String type,
            CategoryType category, Long cursorId, Pageable pageable
    );

    List<User> getRecommendUsers(List<User> myFollowingUsers, User user);

    Long getCollaboratorCount(String search, Long loginUserId);

    Slice<UserSearchResponse> getCollaborators(String search, Pageable pageable, Long loginUserId);
}
