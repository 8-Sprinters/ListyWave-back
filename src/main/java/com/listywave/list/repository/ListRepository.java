package com.listywave.list.repository;

import com.listywave.list.application.domain.Lists;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<Lists, Long> {
}
