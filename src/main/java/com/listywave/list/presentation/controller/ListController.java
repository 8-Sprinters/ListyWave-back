package com.listywave.list.presentation.controller;

import com.listywave.common.auth.Auth;
import com.listywave.common.auth.OptionalAuth;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.SortType;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.list.application.dto.response.ListRecentResponse;
import com.listywave.list.application.dto.response.ListSearchResponse;
import com.listywave.list.application.dto.response.RecommendedListResponse;
import com.listywave.list.application.service.ListService;
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import com.listywave.list.presentation.dto.request.ListUpdateRequest;
import com.listywave.list.presentation.dto.request.ListsDeleteRequest;
import com.listywave.list.presentation.dto.request.ReactionRequest;
import com.listywave.user.application.dto.FindFeedListResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ListController {

    private final ListService listService;

    @PostMapping("/lists")
    ResponseEntity<ListCreateResponse> listCreate(
            @RequestBody ListCreateRequest request,
            @Auth Long loginUserId
    ) {
        ListCreateResponse response = listService.listCreate(request, loginUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/lists/{listId}")
    ResponseEntity<ListDetailResponse> getListDetail(
            @PathVariable Long listId,
            @OptionalAuth Long loginUserId
    ) {
        ListDetailResponse listDetailResponse = listService.getListDetail(listId, loginUserId);
        return ResponseEntity.ok(listDetailResponse);
    }

    @GetMapping("/lists/recommended")
    ResponseEntity<List<RecommendedListResponse>> getRecommendedLists() {
        List<RecommendedListResponse> recommendedLists = listService.getRecommendedLists();
        return ResponseEntity.ok().body(recommendedLists);
    }

    @DeleteMapping("/lists/{listId}")
    ResponseEntity<Void> deleteList(
            @PathVariable("listId") Long listId,
            @Auth Long loginUserId
    ) {
        listService.deleteList(listId, loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lists")
    ResponseEntity<ListRecentResponse> getRecentLists(
            @RequestParam(name = "cursorUpdatedDate", required = false) LocalDateTime cursorUpdatedDate,
            @RequestParam(name = "category", defaultValue = "entire") CategoryType category,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        ListRecentResponse recentLists = listService.getRecentLists(cursorUpdatedDate, category, pageable);
        return ResponseEntity.ok(recentLists);
    }

    @GetMapping("/lists/following")
    ResponseEntity<ListRecentResponse> getRecentListsByFollowing(
            @Auth Long loginUserId,
            @RequestParam(name = "cursorUpdatedDate", required = false) LocalDateTime cursorUpdatedDate,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        ListRecentResponse recentLists = listService.getRecentListsByFollowing(loginUserId, cursorUpdatedDate, pageable);
        return ResponseEntity.ok(recentLists);
    }

    @GetMapping("/lists/search")
    ResponseEntity<ListSearchResponse> search(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "sort", defaultValue = "new") SortType sort,
            @RequestParam(value = "category", defaultValue = "entire") CategoryType category,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "cursorId", defaultValue = "0") Long cursorId
    ) {
        ListSearchResponse response = listService.search(keyword, sort, category, size, cursorId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/lists/{listId}")
    ResponseEntity<Void> update(
            @PathVariable("listId") Long listId,
            @Auth Long loginUserId,
            @RequestBody ListUpdateRequest request
    ) {
        listService.update(listId, loginUserId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/lists")
    ResponseEntity<FindFeedListResponse> getAllListOfUser(
            @PathVariable("userId") Long targetUserId,
            @RequestParam(name = "type", defaultValue = "my") String type,
            @RequestParam(name = "category", defaultValue = "entire") CategoryType category,
            @RequestParam(name = "cursorUpdatedDate", required = false) LocalDateTime cursorUpdatedDate,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        FindFeedListResponse result
                = listService.findFeedList(targetUserId, type, category, cursorUpdatedDate, pageable);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/lists")
    ResponseEntity<Void> deleteLists(
            @Auth Long userId,
            @RequestBody ListsDeleteRequest request
    ) {
        listService.deleteLists(userId, request.listId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/lists/{listId}/visibility")
    ResponseEntity<Void> changeVisibility(
            @Auth Long loginUserId,
            @PathVariable("listId") Long listId
    ) {
        listService.changeVisibility(loginUserId, listId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lists/{listId}/reaction")
    public ResponseEntity<String> handleReaction(
            @Auth Long loginUserId,
            @PathVariable("listId") Long listId,
            @RequestBody ReactionRequest request
    ) {
        listService.handleReaction(loginUserId, listId, request.reaction());
        return ResponseEntity.noContent().build();
    }
}
