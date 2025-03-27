package com.listywave.topic.presentation;

import com.listywave.common.auth.Auth;
import com.listywave.topic.application.service.TopicService;
import com.listywave.topic.application.service.dto.ExposedTopicFindResponse;
import com.listywave.topic.application.service.dto.RecommendTopicFindResponse;
import com.listywave.topic.application.service.dto.TopicCreateRequest;
import com.listywave.topic.application.service.dto.TopicFindResponse;
import com.listywave.topic.presentation.dto.TopicUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping("/topics")
    ResponseEntity<Void> create(@RequestBody TopicCreateRequest request, @Auth Long userId) {
        topicService.create(request, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/topics")
    ResponseEntity<ExposedTopicFindResponse> findAllExposed(
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size
    ) {
        ExposedTopicFindResponse result = topicService.findAllExposed(cursorId, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/topics/recommend")
    ResponseEntity<List<RecommendTopicFindResponse>> recommendTopics(
            @RequestParam(name = "size", required = false, defaultValue = "5") int size
    ) {
        List<RecommendTopicFindResponse> result = topicService.getRecommendTopics(size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/topics")
    ResponseEntity<TopicFindResponse> findAll(
            @Auth Long adminId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "5") int size
    ) {
        TopicFindResponse result = topicService.findAll(adminId, cursorId, size);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/admin/topics/{topicId}")
    ResponseEntity<Void> update(@Auth Long adminId, @PathVariable Long topicId, @RequestBody TopicUpdateRequest request) {
        topicService.update(topicId, request.isExposed(), request.categoryCode(), request.title());
        return ResponseEntity.noContent().build();
    }
}
