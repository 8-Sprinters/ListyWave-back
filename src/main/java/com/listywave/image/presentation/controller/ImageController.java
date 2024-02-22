package com.listywave.image.presentation.controller;

import com.listywave.common.auth.Auth;
import com.listywave.image.application.domain.BasicBackgroundImage;
import com.listywave.image.application.domain.BasicProfileImage;
import com.listywave.image.application.dto.response.BasicBackgroundImageUrlResponse;
import com.listywave.image.application.dto.response.BasicProfileImageUrlResponse;
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
            @RequestBody ListsImagesCreateRequest request
    ) {
        List<ItemPresignedUrlResponse> response = imageService.createListsPresignedUrl(
                request.ownerId(),
                request.listId(),
                request.extensionRanks()
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/lists/upload-complete")
    ResponseEntity<Void> listItemImagesUpload(
            @RequestBody ListsImagesCreateRequest request
    ) {
        imageService.uploadCompleteItemImages(request.ownerId(), request.listId(), request.extensionRanks());
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

    @GetMapping("/users/basic-profile-image")
    ResponseEntity<List<BasicProfileImageUrlResponse>> getAllBasicProfileImageUrl() {
        List<BasicProfileImageUrlResponse> response = Arrays.stream(BasicProfileImage.values())
                .map(BasicProfileImageUrlResponse::of)
                .toList();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/users/basic-background-image")
    ResponseEntity<List<BasicBackgroundImageUrlResponse>> getAllBasicBackgroundImageUrl() {
        List<BasicBackgroundImageUrlResponse> response = Arrays.stream(BasicBackgroundImage.values())
                .map(BasicBackgroundImageUrlResponse::of)
                .toList();
        return ResponseEntity.ok().body(response);
    }

}
