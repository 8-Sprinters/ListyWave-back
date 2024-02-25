package com.listywave.collection.repository.custom;

import com.listywave.collection.application.domain.Collect;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomCollectionRepository {

    Slice<Collect> getAllCollectionList(Long cursorId, Pageable pageable, Long userId, CategoryType category);

    List<CategoryType> getCategoriesByCollect(User user);
}
