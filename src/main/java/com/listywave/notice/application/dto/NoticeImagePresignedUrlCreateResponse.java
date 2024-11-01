package com.listywave.notice.application.dto;

public record NoticeImagePresignedUrlCreateResponse(
        int order,
        String presignedUrl
) {

    public static NoticeImagePresignedUrlCreateResponse of(int order, String presignedUrl) {
        return new NoticeImagePresignedUrlCreateResponse(order, presignedUrl);
    }
}
