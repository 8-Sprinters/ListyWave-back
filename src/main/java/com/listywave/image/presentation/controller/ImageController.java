package com.listywave.image.presentation.controller;

import com.listywave.common.auth.Auth;
import com.listywave.image.application.domain.DefaultBackgroundImages;
import com.listywave.image.application.domain.DefaultProfileImages;
import com.listywave.image.application.dto.response.DefaultBackgroundImageUrlResponse;
import com.listywave.image.application.dto.response.DefaultProfileImageUrlResponse;
import com.listywave.image.application.dto.response.ListItemPresignedUrlResponse;
import com.listywave.image.application.dto.response.UserPresignedUrlCreateResponse;
import com.listywave.image.application.service.ImageService;
import com.listywave.image.presentation.dto.request.ListImagesCreateRequest;
import com.listywave.image.presentation.dto.request.UserImageUpdateRequest;
import com.listywave.notice.application.dto.NoticeImagePresignedUrlCreateResponse;
import com.listywave.notice.application.dto.OrderAndExtensionDto;
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
    ResponseEntity<List<ListItemPresignedUrlResponse>> createPresignedUrlOfItem(
            @RequestBody ListImagesCreateRequest request,
            @Auth Long userId
    ) {
        var response = imageService.createPresignedUrlOfItem(userId, request.listId(), request.extensionRanks());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/lists/upload-complete")
    ResponseEntity<Void> completeUploadItems(@RequestBody ListImagesCreateRequest request, @Auth Long userId) {
        imageService.updateAllItemsImageUrl(userId, request.listId(), request.extensionRanks());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/upload-url")
    ResponseEntity<UserPresignedUrlCreateResponse> createPresignedUrlOfUserImage(
            @RequestBody UserImageUpdateRequest request,
            @Auth Long userId
    ) {
        var userPresignedUrlResponse = imageService.createPresignedUrlOfUserImage(request.profileExtension(), request.backgroundExtension(), userId);
        return ResponseEntity.ok(userPresignedUrlResponse);
    }

    @PostMapping("/users/upload-complete")
    ResponseEntity<Void> completeUploadUserImage(
            @RequestBody UserImageUpdateRequest request,
            @Auth Long userId
    ) {
        imageService.updateUserImages(request.profileExtension(), request.backgroundExtension(), userId);
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

    @PostMapping("/admin/notices/{noticeId}/presigned-url")
    ResponseEntity<List<NoticeImagePresignedUrlCreateResponse>> createPresignedUrlOfNoticeContent(
            @PathVariable Long noticeId,
            @RequestBody List<OrderAndExtensionDto> request
    ) {
        var result = imageService.createNoticeImagePresignedUrl(noticeId, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/notices/{noticeId}/upload-complete")
    ResponseEntity<Void> completeUploadNoticeImages(
            @PathVariable Long noticeId,
            @RequestBody List<OrderAndExtensionDto> requests
    ) {
        imageService.updateNoticeContentImages(noticeId, requests);
        return ResponseEntity.noContent().build();
    }
}
