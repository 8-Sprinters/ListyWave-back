package com.listywave.list.repository.list.custom;

import com.listywave.list.application.domain.Lists;
import java.util.List;

public interface CustomListRepository {

    List<Lists> findTrandingLists();
}
