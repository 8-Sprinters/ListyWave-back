package com.listywave.collection.presentation.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.collection.application.dto.CollectionResponse;
import com.listywave.collection.application.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/lists/{listId}/collect")
    ResponseEntity<Void> collectOrCancel(
            @PathVariable("listId") Long listId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        collectionService.collectOrCancel(listId, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lists/collect")
    ResponseEntity<CollectionResponse> getCollection(
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken,
            @RequestParam(name = "cursorId", required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        CollectionResponse collection = collectionService.getCollection(accessToken, cursorId, pageable);
        return ResponseEntity.ok(collection);
    }
}
