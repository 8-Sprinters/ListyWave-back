package com.listywave.image.application.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageFileExtension {

    JPEG("jpeg"),
    JPG("jpg"),
    PNG("png"),
    ;

    private final String uploadExtension;

    @JsonCreator
    public static ImageFileExtension fromString(String key) {
        return Arrays.stream(ImageFileExtension.values())
                .filter(extensionType -> extensionType.name().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 이미지 확장자가 존재하지 않습니다."));
    }
}
