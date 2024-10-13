package com.listywave.collection.presentation.controller;

import com.listywave.collection.application.dto.CollectionResponse;
import com.listywave.collection.application.service.CollectionService;
import com.listywave.collection.presentation.dto.FolderSelectionRequest;
import com.listywave.common.auth.Auth;
import com.listywave.list.application.dto.response.CategoryTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/lists/{listId}/collect")
    ResponseEntity<Void> collectOrCancel(
            @PathVariable("listId") Long listId,
            @RequestBody FolderSelectionRequest request,
            @Auth Long loginUserId
    ) {
        collectionService.collectOrCancel(listId, request.folderId(), loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/folder/{folderId}/collections")
    ResponseEntity<CollectionResponse> getCollection(
            @Auth Long loginUserId,
            @PathVariable("folderId") Long folderId,
            @RequestParam(name = "cursorId", required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        CollectionResponse collection = collectionService.getCollection(loginUserId, cursorId, pageable, folderId);
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/collection/categories")
    ResponseEntity<List<CategoryTypeResponse>> getCategoriesOfCollection(
            @Auth Long loginUserId
    ) {
        List<CategoryTypeResponse> categories = collectionService.getCategoriesOfCollection(loginUserId);
        return ResponseEntity.ok().body(categories);
    }
}
