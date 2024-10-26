package com.listywave.topic.presentation;

import com.listywave.common.auth.Auth;
import com.listywave.topic.application.service.TopicService;
import com.listywave.topic.application.service.dto.ExposedTopicFindResponse;
import com.listywave.topic.application.service.dto.TopicCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
