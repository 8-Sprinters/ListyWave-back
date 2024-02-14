package com.listywave.collection.presentation.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.collection.application.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/collect/{userId}/{listId}")
    ResponseEntity<Void> collectOrCancel(
            @PathVariable("userId") Long userId,
            @PathVariable("listId") Long listId,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        collectionService.collectOrCancel(userId, listId, accessToken);
        return ResponseEntity.noContent().build();
    }
}