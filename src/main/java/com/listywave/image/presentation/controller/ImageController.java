package com.listywave.image.presentation.controller;

import com.listywave.image.application.service.ImageService;
import com.listywave.image.presentation.dto.request.ListsImagesCreateRequest;
import com.listywave.image.presentation.dto.response.PresignedUrlResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/lists/upload-url")
    public ResponseEntity<List<PresignedUrlResponse>> listItemPresignedUrlCreate(
            @RequestBody ListsImagesCreateRequest request
    ) {
        return ResponseEntity.ok()
                .body(imageService.createListsPresignedUrl(
                        request.ownerId(),
                        request.listId(),
                        request.extensionRanks()
                ));
    }

    @PostMapping("/lists/upload-complete")
    public void listItemImagesUpload(@RequestBody ListsImagesCreateRequest request) {
        imageService.uploadCompleteItemImages(request.ownerId(), request.listId(), request.extensionRanks());
    }
}
