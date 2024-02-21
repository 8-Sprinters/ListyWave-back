package com.listywave.user.repository.user.custom;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.application.dto.CollaboratorResponse;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomUserRepository {

    Slice<ListEntity> findFeedLists(
            List<Collaborator> listBycollabo, Long userId, String type,
            CategoryType category, Long cursorId, Pageable pageable
    );

    List<User> getRecommendUsers(List<User> myFollowingUsers, User user);

    Long getCollaboratorCount(String search, User user);

    Slice<CollaboratorResponse> getCollaborators(String search, Pageable pageable, User user);
}
