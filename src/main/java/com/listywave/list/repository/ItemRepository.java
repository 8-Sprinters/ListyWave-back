package com.listywave.list.repository;

import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByListIdAndRanking(Long listId, int ranking);

    @Modifying
    @Query("delete from Item i where i.list in :lists")
    void deleteAllListIn(@Param("lists") List<ListEntity> lists);
}
