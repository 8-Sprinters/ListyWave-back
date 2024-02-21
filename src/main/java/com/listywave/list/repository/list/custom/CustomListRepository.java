package com.listywave.list.repository.list.custom;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomListRepository {

    List<ListEntity> findTrandingLists();

    Slice<ListEntity> getRecentLists(Long cursorId, Pageable pageable);

    Slice<ListEntity> getRecentListsByFollowing(List<User> followingUsers, Long cursorId, Pageable pageable);
}
