package com.listywave.history.repository;

import com.listywave.history.application.domain.History;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("""
            select h from History h
            left join fetch ListEntity l on h.list = l
            where h.list = :list
            order by h.id
            """)
    List<History> findAllByList(ListEntity list);

    void deleteAllByList(ListEntity list);
}
