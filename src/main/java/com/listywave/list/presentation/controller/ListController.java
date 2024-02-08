package com.listywave.list.presentation.controller;

import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.list.application.dto.response.ListRecentResponse;
import com.listywave.list.application.dto.response.ListTrandingResponse;
import com.listywave.list.application.service.ListService;
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lists")
public class ListController {

    private final ListService listService;

    @PostMapping
    ResponseEntity<ListCreateResponse> listCreate(@RequestBody ListCreateRequest request) {
        ListCreateCommand listCreateCommand = request.toCommand();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listService.listCreate(
                        listCreateCommand,
                        request.labels(),
                        request.collaboratorIds(),
                        request.items()
                ));
    }

    @GetMapping("/{listId}")
    ResponseEntity<ListDetailResponse> getListDetail(
            @PathVariable Long listId,
            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken
    ) {
        ListDetailResponse listDetailResponse = listService.getListDetail(listId, accessToken);
        return ResponseEntity.ok(listDetailResponse);
    }

    @GetMapping("/explore")
    ResponseEntity<List<ListTrandingResponse>> getTrandingList() {
        List<ListTrandingResponse> trandingList = listService.getTrandingList();
        return ResponseEntity.ok().body(trandingList);
    }

    @DeleteMapping("/{listId}")
    ResponseEntity<Void> deleteList(@PathVariable(value = "listId") Long listId) {
        listService.deleteList(listId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    ResponseEntity<ListRecentResponse> getRecentLists(
            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken
    ) {
        ListRecentResponse recentLists = listService.getRecentLists(accessToken);
        return ResponseEntity.ok(recentLists);
    }
}
