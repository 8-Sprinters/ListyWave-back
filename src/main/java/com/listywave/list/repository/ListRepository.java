package com.listywave.list.repository;

import static com.listywave.common.exception.ErrorCode.NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.Lists;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<Lists, Long> {

    default Lists getById(Long listId) {
        return findById(listId).orElseThrow(() -> new CustomException(NOT_FOUND));
    }
}
