package com.listywave.collection.presentation.controller;

import com.listywave.collection.application.dto.CollectionResponse;
import com.listywave.collection.application.service.CollectionService;
import com.listywave.common.auth.Auth;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.dto.response.CategoryTypeResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/lists/{listId}/collect")
    ResponseEntity<Void> collectOrCancel(
            @PathVariable("listId") Long listId,
            @Auth Long loginUserId
    ) {
        collectionService.collectOrCancel(listId, loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lists/collect")
    ResponseEntity<CollectionResponse> getCollection(
            @Auth Long loginUserId,
            @RequestParam(name = "category", defaultValue = "entire") CategoryType category,
            @RequestParam(name = "cursorId", required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        CollectionResponse collection = collectionService.getCollection(loginUserId, cursorId, pageable, category);
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
