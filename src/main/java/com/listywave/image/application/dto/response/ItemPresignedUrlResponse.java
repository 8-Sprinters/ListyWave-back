package com.listywave.image.application.dto.response;

import lombok.Builder;

@Builder
public record ItemPresignedUrlResponse(int rank, String presignedUrl) {
    public static ItemPresignedUrlResponse of(int rank, String presignedUrl) {
        return ItemPresignedUrlResponse.builder()
                .rank(rank)
                .presignedUrl(presignedUrl)
                .build();
    }
}
