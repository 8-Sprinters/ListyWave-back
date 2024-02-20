package com.listywave.collaborator.presentation.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.collaborator.application.dto.CollaboratorSearchResponse;
import com.listywave.collaborator.application.service.CollaboratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    @GetMapping("/collaborators")
    ResponseEntity<CollaboratorSearchResponse> getCollaborators(
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken,
            @RequestParam(name = "search", defaultValue = "") String search,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        CollaboratorSearchResponse collaborators = collaboratorService.getCollaborators(accessToken, search, pageable);
        return ResponseEntity.ok(collaborators);
    }
}
