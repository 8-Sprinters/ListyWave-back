package com.listywave.list.presentation.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.listywave.list.application.dto.response.CommentCreateResponse;
import com.listywave.list.application.service.CommentService;
import com.listywave.list.presentation.dto.request.comment.CommentCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lists/{listId}")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    ResponseEntity<CommentCreateResponse> create(
            @PathVariable(value = "listId") Long listId,
            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken,
            @RequestBody CommentCreateRequest commentCreateRequest
    ) {
        CommentCreateResponse response = commentService.create(listId, accessToken, commentCreateRequest.content());
        return ResponseEntity.status(CREATED).body(response);
    }
}
