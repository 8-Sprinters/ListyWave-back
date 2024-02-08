package com.listywave.list.presentation.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.listywave.list.application.dto.response.CommentCreateResponse;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.application.service.CommentService;
import com.listywave.list.presentation.dto.request.comment.CommentCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lists/{listId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    ResponseEntity<CommentCreateResponse> create(
            @PathVariable(value = "listId") Long listId,
//            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken,
            @RequestBody CommentCreateRequest commentCreateRequest
    ) {
        CommentCreateResponse response = commentService.create(listId, commentCreateRequest.content());
        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping
    ResponseEntity<CommentFindResponse> getAllCommentsByList(
            @PathVariable(value = "listId") Long listId,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "cursorId", defaultValue = "0") Long cursorId
    ) {
        CommentFindResponse response = commentService.getComments(listId, size, cursorId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    ResponseEntity<Void> delete(
            @PathVariable(value = "listId") Long listId,
//            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken,
            @PathVariable(value = "commentId") Long commentId
    ) {
        commentService.delete(listId, commentId);
        return ResponseEntity.noContent().build();
    }
}
