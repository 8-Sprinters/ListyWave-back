package com.listywave.list.repository.list.custom;

import com.listywave.list.application.domain.Lists;
import com.listywave.user.application.domain.User;
import java.util.List;

public interface CustomListRepository {

    List<Lists> findTrandingLists();

    List<Lists> getRecentLists();

    List<Lists> getRecentListsByFollowing(List<User> followingUsers);
}
