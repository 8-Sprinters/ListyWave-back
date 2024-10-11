package com.listywave.list.presentation.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.listywave.common.auth.Auth;
import com.listywave.list.application.dto.response.CommentCreateResponse;
import com.listywave.list.application.dto.response.CommentFindResponse;
import com.listywave.list.application.service.CommentService;
import com.listywave.list.presentation.dto.request.comment.CommentCreateRequest;
import com.listywave.list.presentation.dto.request.comment.CommentUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/lists/{listId}/comments")
    ResponseEntity<CommentCreateResponse> create(
            @PathVariable("listId") Long listId,
            @Auth Long writerId,
            @RequestBody CommentCreateRequest request
    ) {
        CommentCreateResponse response = commentService.create(listId, writerId, request.content(), request.mentionedIds());
        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping("/lists/{listId}/comments")
    ResponseEntity<CommentFindResponse> getAllCommentsByList(
            @PathVariable("listId") Long listId,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "cursorId", required = false) Long cursorId
    ) {
        CommentFindResponse response = commentService.findCommentBy(listId, size, cursorId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/lists/{listId}/comments/{commentId}")
    ResponseEntity<Void> delete(
            @PathVariable("listId") Long listId,
            @Auth Long loginUserId,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.delete(listId, commentId, loginUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/lists/{listId}/comments/{commentId}")
    ResponseEntity<Void> update(
            @PathVariable("listId") Long listId,
            @Auth Long loginUserId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentUpdateRequest commentUpdateRequest
    ) {
        commentService.update(listId, commentId, loginUserId, commentUpdateRequest.content());
        return ResponseEntity.noContent().build();
    }
}
