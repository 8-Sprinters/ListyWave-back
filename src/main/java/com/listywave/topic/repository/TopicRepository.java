package com.listywave.topic.repository;

import com.listywave.topic.application.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long>, CustomTopicRepository {
}
