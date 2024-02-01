package com.listywave.list.presentation.controller;

import com.listywave.list.application.dto.ListCreateCommand;
import com.listywave.list.application.service.ListService;
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import com.listywave.list.presentation.dto.response.ListCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lists")
public class ListController {

    private final ListService listService;

    @PostMapping
    public ResponseEntity<ListCreateResponse> listCreate(@RequestBody ListCreateRequest request) {
        ListCreateCommand listCreateCommand = request.toCommand();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listService.listCreate(
                        listCreateCommand,
                        request.labels(),
                        request.collaboratorIds(),
                        request.items()
                ));
    }
}
