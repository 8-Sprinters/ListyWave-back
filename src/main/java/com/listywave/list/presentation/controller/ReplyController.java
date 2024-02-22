package com.listywave.list.presentation.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.listywave.common.auth.Auth;
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
            @Auth Long loginUserId,
            @RequestBody ReplyCreateRequest request
    ) {
        ReplyCreateResponse response = replyService.createReply(listId, commentId, request.content(), loginUserId);
        return ResponseEntity.status(CREATED).body(response);
    }

    @DeleteMapping("/{replyId}")
    ResponseEntity<Void> deleteReply(
            @PathVariable("listId") Long listId,
            @PathVariable("commentId") Long commentId,
            @PathVariable("replyId") Long replyId,
            @Auth Long loginUserId
    ) {
        ReplyDeleteCommand replyDeleteCommand = new ReplyDeleteCommand(listId, commentId, replyId);
        replyService.delete(replyDeleteCommand, loginUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{replyId}")
    ResponseEntity<Void> updateReply(
            @PathVariable("listId") Long listId,
            @PathVariable("commentId") Long commentId,
            @PathVariable("replyId") Long replyId,
            @Auth Long loginUserId,
            @RequestBody ReplyUpdateRequest request
    ) {
        ReplyUpdateCommand replyUpdateCommand = new ReplyUpdateCommand(listId, commentId, replyId, request.content());
        replyService.update(replyUpdateCommand, loginUserId);
        return ResponseEntity.noContent().build();
    }
}
