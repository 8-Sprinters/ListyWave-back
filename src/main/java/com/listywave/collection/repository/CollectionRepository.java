package com.listywave.collection.repository;

import com.listywave.collection.application.domain.Collect;
import com.listywave.list.application.domain.list.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collect, Long> {

    boolean existsByListAndUserId(ListEntity list, Long userId);

    void deleteByListAndUserId(ListEntity list, Long userId);
}
