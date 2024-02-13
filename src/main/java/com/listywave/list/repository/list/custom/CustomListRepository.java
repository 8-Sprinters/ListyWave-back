package com.listywave.list.repository.list.custom;

import com.listywave.list.application.domain.ListEntity;
import com.listywave.user.application.domain.User;
import java.util.List;

public interface CustomListRepository {

    List<ListEntity> findTrandingLists();

    List<ListEntity> getRecentLists();

    List<ListEntity> getRecentListsByFollowing(List<User> followingUsers);
}
