package com.listywave.collection.presentation.controller;

import com.listywave.collection.application.dto.FolderCreateResponse;
import com.listywave.collection.application.dto.FolderListResponse;
import com.listywave.collection.application.service.FolderService;
import com.listywave.collection.presentation.dto.FolderCreateRequest;
import com.listywave.collection.presentation.dto.FolderUpdateRequest;
import com.listywave.common.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/collect/folder")
    ResponseEntity<FolderCreateResponse> create(
            @Auth Long loginUserId,
            @RequestBody FolderCreateRequest request
    ) {
        FolderCreateResponse response = folderService.createFolder(loginUserId, request.folderName());
        return ResponseEntity.status(CREATED).body(response);
    }

    @PutMapping("/collect/folder/{folderId}")
    ResponseEntity<Void> update(
            @Auth Long loginUserId,
            @PathVariable("folderId") Long folderId,
            @RequestBody FolderUpdateRequest request
    ) {
        folderService.updateFolder(loginUserId, folderId, request.folderName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/collect/folder/{folderId}")
    ResponseEntity<Void> delete(
            @Auth Long loginUserId,
            @PathVariable("folderId") Long folderId
    ) {
        folderService.deleteFolder(loginUserId, folderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/collect/folder")
    ResponseEntity<FolderListResponse> getFolders(
            @Auth Long loginUserId
    ) {
        FolderListResponse response = folderService.getFolders(loginUserId);
        return ResponseEntity.ok(response);
    }
}
