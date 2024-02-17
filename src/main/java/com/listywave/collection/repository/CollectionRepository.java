package com.listywave.collection.repository;

import com.listywave.collaborator.repository.custom.CustomCollectionRepository;
import com.listywave.collection.application.domain.Collect;
import com.listywave.list.application.domain.list.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collect, Long>, CustomCollectionRepository {

    boolean existsByListAndUserId(ListEntity list, Long userId);

    void deleteByListAndUserId(ListEntity list, Long userId);
}
