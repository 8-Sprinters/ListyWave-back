package com.listywave.image.application.dto.response;

public record UserPresignedUrlCreateResponse(
        Long userId,
        String profilePresignedUrl,
        String backgroundPresignedUrl
) {

    public static UserPresignedUrlCreateResponse of(Long userId, String profilePresignedUrl, String backgroundPresignedUrl) {
        return new UserPresignedUrlCreateResponse(userId, profilePresignedUrl, backgroundPresignedUrl);
    }
}
