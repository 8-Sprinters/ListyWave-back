package com.listywave.image.presentation.controller;

import com.listywave.common.auth.Auth;
import com.listywave.image.application.domain.DefaultBackgroundImages;
import com.listywave.image.application.domain.DefaultProfileImages;
import com.listywave.image.application.dto.response.DefaultBackgroundImageUrlResponse;
import com.listywave.image.application.dto.response.DefaultProfileImageUrlResponse;
import com.listywave.image.application.dto.response.ItemPresignedUrlResponse;
import com.listywave.image.application.dto.response.UserPresignedUrlResponse;
import com.listywave.image.application.service.ImageService;
import com.listywave.image.presentation.dto.request.ListsImagesCreateRequest;
import com.listywave.image.presentation.dto.request.UserImageUpdateRequest;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/lists/upload-url")
    ResponseEntity<List<ItemPresignedUrlResponse>> listItemPresignedUrlCreate(
            @RequestBody ListsImagesCreateRequest request,
            @Auth Long loginUserId
    ) {
        List<ItemPresignedUrlResponse> response = imageService.createListsPresignedUrl(
                loginUserId,
                request.listId(),
                request.extensionRanks()
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/lists/upload-complete")
    ResponseEntity<Void> listItemImagesUpload(
            @RequestBody ListsImagesCreateRequest request,
            @Auth Long loginUserId
    ) {
        imageService.uploadCompleteItemImages(loginUserId, request.listId(), request.extensionRanks());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/upload-url")
    ResponseEntity<UserPresignedUrlResponse> userImagePresignedUrlCreate(
            @RequestBody UserImageUpdateRequest request,
            @Auth Long loginUserId
    ) {
        UserPresignedUrlResponse userPresignedUrlResponse = imageService.updateUserImagePresignedUrl(
                request.profileExtension(),
                request.backgroundExtension(),
                loginUserId
        );
        return ResponseEntity.ok(userPresignedUrlResponse);
    }

    @PostMapping("/users/upload-complete")
    ResponseEntity<Void> userImageUpload(
            @RequestBody UserImageUpdateRequest request,
            @Auth Long loginUserId
    ) {
        imageService.uploadCompleteUserImages(
                request.profileExtension(),
                request.backgroundExtension(),
                loginUserId
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lists/{listId}/items/{itemId}")
    ResponseEntity<Void> deleteImageOfItem(
            @PathVariable("listId") Long listId,
            @PathVariable("itemId") Long itemId,
            @Auth Long loginUserId
    ) {
        imageService.deleteImageOfItem(listId, itemId, loginUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/default-profile-images")
    ResponseEntity<List<DefaultProfileImageUrlResponse>> getAllDefaultProfileImageUrl() {
        List<DefaultProfileImageUrlResponse> response = Arrays.stream(DefaultProfileImages.values())
                .map(DefaultProfileImageUrlResponse::of)
                .toList();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/users/default-background-images")
    ResponseEntity<List<DefaultBackgroundImageUrlResponse>> getAllDefaultBackgroundImageUrl() {
        List<DefaultBackgroundImageUrlResponse> response = Arrays.stream(DefaultBackgroundImages.values())
                .map(DefaultBackgroundImageUrlResponse::of)
                .toList();
        return ResponseEntity.ok().body(response);
    }

}
