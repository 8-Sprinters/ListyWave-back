package com.listywave.topic.repository;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.listywave.common.exception.CustomException;
import com.listywave.topic.application.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long>, CustomTopicRepository {

    default Topic getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
    }
}
