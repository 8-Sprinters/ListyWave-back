package com.listywave.history.presentation;

import com.listywave.history.application.dto.HistorySearchResponse;
import com.listywave.history.application.service.HistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/lists/{listId}/histories")
    ResponseEntity<List<HistorySearchResponse>> searchHistories(
            @PathVariable("listId") Long listId
    ) {
        List<HistorySearchResponse> result = historyService.searchHistories(listId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/lists/{listId}/histories/{historyId}")
    ResponseEntity<Void> updatePublic(
            @PathVariable("listId") Long listId,
            @PathVariable("historyId") Long historyId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        historyService.updatePublic(historyId, accessToken);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lists/{listId}/histories/{historyId}")
    ResponseEntity<Void> deleteHistory(
            @PathVariable("listId") Long listId,
            @PathVariable("historyId") Long historyId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        historyService.deleteHistory(historyId, accessToken);
        return ResponseEntity.noContent().build();
    }
}
