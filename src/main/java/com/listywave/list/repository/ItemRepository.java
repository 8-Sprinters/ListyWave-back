package com.listywave.list.repository;

import com.listywave.list.application.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByListIdAndRanking(Long listId, int ranking);
}
