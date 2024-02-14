package com.listywave.list.repository.list;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.ListEntity;
import com.listywave.list.repository.list.custom.CustomListRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<ListEntity, Long>, CustomListRepository {

    default ListEntity getById(Long listId) {
        return findById(listId).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
    }
}
