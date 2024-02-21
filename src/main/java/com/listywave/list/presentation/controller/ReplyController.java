package com.listywave.list.presentation.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;

import com.listywave.list.application.dto.ReplyDeleteCommand;
import com.listywave.list.application.dto.ReplyUpdateCommand;
import com.listywave.list.application.dto.response.ReplyCreateResponse;
import com.listywave.list.application.service.ReplyService;
import com.listywave.list.presentation.dto.request.ReplyCreateRequest;
import com.listywave.list.presentation.dto.request.ReplyUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lists/{listId}/comments/{commentId}/replies")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    ResponseEntity<ReplyCreateResponse> create(
            @PathVariable("listId") Long listId,
            @PathVariable("commentId") Long commentId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken,
            @RequestBody ReplyCreateRequest request
    ) {
        ReplyCreateResponse response = replyService.createReply(listId, commentId, request.content(), accessToken);
        return ResponseEntity.status(CREATED).body(response);
    }

    @DeleteMapping("/{replyId}")
    ResponseEntity<Void> deleteReply(
            @PathVariable("listId") Long listId,
            @PathVariable("commentId") Long commentId,
            @PathVariable("replyId") Long replyId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        ReplyDeleteCommand replyDeleteCommand = new ReplyDeleteCommand(listId, commentId, replyId);
        replyService.delete(replyDeleteCommand, accessToken);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{replyId}")
    ResponseEntity<Void> updateReply(
            @PathVariable("listId") Long listId,
            @PathVariable("commentId") Long commentId,
            @PathVariable("replyId") Long replyId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken,
            @RequestBody ReplyUpdateRequest request
    ) {
        ReplyUpdateCommand replyUpdateCommand = new ReplyUpdateCommand(listId, commentId, replyId, request.content());
        replyService.update(replyUpdateCommand, accessToken);
        return ResponseEntity.noContent().build();
    }
}
