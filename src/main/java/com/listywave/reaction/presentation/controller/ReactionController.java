package com.listywave.reaction.presentation.controller;

import com.listywave.common.auth.Auth;
import com.listywave.reaction.application.service.ReactionService;
import com.listywave.reaction.presentation.dto.request.ReactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/lists/{listId}/reaction")
    public ResponseEntity<String> react(
            @Auth Long loginUserId,
            @PathVariable("listId") Long listId,
            @RequestBody ReactionRequest request
    ) {
        reactionService.react(loginUserId, listId, request.reaction());
        return ResponseEntity.noContent().build();
    }
}
