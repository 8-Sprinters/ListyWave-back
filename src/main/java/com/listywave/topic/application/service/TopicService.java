package com.listywave.topic.application.service;

import com.listywave.topic.application.domain.Topic;
import com.listywave.topic.application.service.dto.ExposedTopicFindResponse;
import com.listywave.topic.application.service.dto.TopicCreateRequest;
import com.listywave.topic.repository.TopicRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TopicService {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    public void create(TopicCreateRequest request, Long userId) {
        User user = userRepository.getById(userId);
        Topic topic = request.toEntity(user);
        topicRepository.save(topic);
    }

    public ExposedTopicFindResponse findAllExposed(@Nullable Long cursorId, int size) {
        List<Topic> result = topicRepository.findAllExposed(cursorId, size);
        return ExposedTopicFindResponse.of(result, size);
    }
}
