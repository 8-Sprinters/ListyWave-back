package com.listywave.image.application.dto.response;

import lombok.Builder;

@Builder
public record UserPresignedUrlResponse(Long ownerId, String profilePresignedUrl, String backgroundPresignedUrl) {

    public static UserPresignedUrlResponse of(Long ownerId, String profilePresignedUrl, String backgroundPresignedUrl) {
        return UserPresignedUrlResponse.builder()
                .ownerId(ownerId)
                .profilePresignedUrl(profilePresignedUrl)
                .backgroundPresignedUrl(backgroundPresignedUrl)
                .build();
    }
}
