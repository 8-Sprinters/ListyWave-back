package com.listywave.list.repository.label;

import com.listywave.list.application.domain.label.Label;
import com.listywave.list.application.domain.list.ListEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabelRepository extends JpaRepository<Label, Long> {

    @Modifying
    @Query("delete from Label l where l.list in :lists")
    void deleteAllListIn(@Param("lists") List<ListEntity> lists);
}
