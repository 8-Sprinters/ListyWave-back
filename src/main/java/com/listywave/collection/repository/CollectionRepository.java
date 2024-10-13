package com.listywave.collection.repository;

import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.application.domain.Folder;
import com.listywave.collection.repository.custom.CustomCollectionRepository;
import com.listywave.list.application.domain.list.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CollectionRepository extends JpaRepository<Collect, Long>, CustomCollectionRepository {

    boolean existsByListAndUserId(ListEntity list, Long userId);

    void deleteByListAndUserId(ListEntity list, Long userId);

    void deleteAllByList(ListEntity list);

    @Modifying
    @Query("delete from Collect c where c.list in :lists")
    void deleteAllByListIn(@Param("lists") List<ListEntity> lists);

    @Modifying
    @Query("delete from Collect c where c.folder =:folder")
    void deleteAllByFolder(@Param("folder") Folder folder);

    List<Collect> findAllByFolder(Folder folder);
}
