package com.listywave.collaborator.presentation.controller;

import com.listywave.collaborator.application.dto.CollaboratorSearchResponse;
import com.listywave.collaborator.application.service.CollaboratorService;
import com.listywave.common.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    @GetMapping("/collaborators")
    ResponseEntity<CollaboratorSearchResponse> getCollaborators(
            @Auth Long loginUserId,
            @RequestParam(name = "search", defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        CollaboratorSearchResponse collaborators = collaboratorService.getCollaborators(loginUserId, search, pageable);
        return ResponseEntity.ok(collaborators);
    }
}
