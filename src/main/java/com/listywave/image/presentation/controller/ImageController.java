package com.listywave.image.presentation.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.image.application.dto.response.ItemPresignedUrlResponse;
import com.listywave.image.application.dto.response.UserPresignedUrlResponse;
import com.listywave.image.application.service.ImageService;
import com.listywave.image.presentation.dto.request.ListsImagesCreateRequest;
import com.listywave.image.presentation.dto.request.UserImageUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/lists/upload-url")
    ResponseEntity<List<ItemPresignedUrlResponse>> listItemPresignedUrlCreate(
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
    ResponseEntity<Void> listItemImagesUpload(@RequestBody ListsImagesCreateRequest request) {
        imageService.uploadCompleteItemImages(request.ownerId(), request.listId(), request.extensionRanks());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/upload-url")
    ResponseEntity<UserPresignedUrlResponse> userImagePresignedUrlCreate(
            @RequestBody UserImageUpdateRequest request,
            @RequestHeader(value = AUTHORIZATION, defaultValue = "") String accessToken
    ) {
        UserPresignedUrlResponse userPresignedUrlResponse = imageService.updateUserImagePresignedUrl(
                request.ownerId(),
                request.profileExtension(),
                request.backgroundExtension(),
                accessToken
        );
        return ResponseEntity.ok(userPresignedUrlResponse);
    }

    @PostMapping("/users/upload-complete")
    ResponseEntity<Void> userImageUpload(
            @RequestBody UserImageUpdateRequest request
    ) {
        imageService.uploadCompleteUserImages(
                request.ownerId(),
                request.profileExtension(),
                request.backgroundExtension()
        );
        return ResponseEntity.noContent().build();
    }
}
