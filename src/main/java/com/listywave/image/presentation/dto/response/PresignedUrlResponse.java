package com.listywave.image.presentation.dto.response;

public record PresignedUrlResponse(int rank, String presignedUrl) {
    public static PresignedUrlResponse of(int rank, String presignedUrl){
        return new PresignedUrlResponse(rank, presignedUrl);
    }
}
