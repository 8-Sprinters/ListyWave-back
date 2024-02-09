package com.listywave.list.presentation.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.listywave.list.application.dto.ReplyDeleteCommand;
import com.listywave.list.application.dto.response.ReplyCreateResponse;
import com.listywave.list.application.service.ReplyService;
import com.listywave.list.presentation.dto.request.ReplyCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lists/{listId}/comments/{commentId}/replies")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    ResponseEntity<ReplyCreateResponse> create(
            @PathVariable(value = "listId") Long listId,
            @PathVariable(value = "commentId") Long commentId,
//            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken,
            @RequestBody ReplyCreateRequest request
    ) {
        ReplyCreateResponse response = replyService.createReply(listId, commentId, request.content());
        return ResponseEntity.status(CREATED).body(response);
    }

    @DeleteMapping("/{replyId}")
    ResponseEntity<Void> deleteReply(
            @PathVariable(value = "listId") Long listId,
            @PathVariable(value = "commentId") Long commentId,
            @PathVariable(value = "replyId") Long replyId
//            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken
    ) {
        ReplyDeleteCommand replyDeleteCommand = new ReplyDeleteCommand(listId, commentId, replyId);
        replyService.delete(replyDeleteCommand);
        return ResponseEntity.noContent().build();
    }
}
