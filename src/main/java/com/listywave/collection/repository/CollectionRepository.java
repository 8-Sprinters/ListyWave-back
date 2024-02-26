package com.listywave.collection.repository;

import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.repository.custom.CustomCollectionRepository;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionRepository extends JpaRepository<Collect, Long>, CustomCollectionRepository {

    boolean existsByListAndUserId(ListEntity list, Long userId);

    void deleteByListAndUserId(ListEntity list, Long userId);

    void deleteAllByList(ListEntity list);

    @Modifying
    @Query("delete from Collect c where c.list in :lists")
    void deleteAllByListIn(@Param("lists") List<ListEntity> lists);
}
