package com.listywave.image.application.dto.response;

public record ListItemPresignedUrlResponse(
        int rank,
        String presignedUrl
) {

    public static ListItemPresignedUrlResponse from(int rank, String presignedUrl) {
        return new ListItemPresignedUrlResponse(rank, presignedUrl);
    }
}
