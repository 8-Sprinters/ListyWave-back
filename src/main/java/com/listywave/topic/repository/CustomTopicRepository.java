package com.listywave.topic.repository;

import com.listywave.topic.application.domain.Topic;
import java.util.List;

public interface CustomTopicRepository {

    List<Topic> findAllExposed(Long cursorId, int size);

    List<Topic> findAll(Long cursorId, int size);
}
