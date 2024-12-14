package com.listywave.topic.application.service;

import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.topic.application.domain.Topic;
import com.listywave.topic.application.service.dto.ExposedTopicFindResponse;
import com.listywave.topic.application.service.dto.RecommendTopicFindResponse;
import com.listywave.topic.application.service.dto.TopicCreateRequest;
import com.listywave.topic.application.service.dto.TopicFindResponse;
import com.listywave.topic.repository.TopicRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public ExposedTopicFindResponse findAllExposed(@Nullable Long cursorId, int size) {
        List<Topic> result = topicRepository.findAllExposed(cursorId, size);
        return ExposedTopicFindResponse.of(result, size);
    }

    @Transactional(readOnly = true)
    public TopicFindResponse findAll(@Nullable Long cursorId, int size) {
        List<Topic> result = topicRepository.findAll(cursorId, size);
        long totalCount = (topicRepository.count() / size) + 1;
        return TopicFindResponse.from(result, size, totalCount);
    }

    public void update(Long topicId, boolean isExposed, String categoryCode, String title) {
        Topic topic = topicRepository.getById(topicId);
        topic.update(isExposed, CategoryType.codeOf(categoryCode), title);
    }

    public List<RecommendTopicFindResponse> getRecommendTopics(int size) {
        List<Topic> topics = topicRepository.getTopicsRandomly(Pageable.ofSize(size));
        return RecommendTopicFindResponse.toList(topics);
    }
}
