package com.listywave.image.application.domain;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.listywave.common.exception.CustomException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageFileExtension {

    JPEG,
    JPG,
    PNG,
    ;

    @JsonCreator
    public static ImageFileExtension ofName(String value) {
        return Arrays.stream(ImageFileExtension.values())
                .filter(extensionType -> extensionType.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "지원하지 않는 이미지 확장자입니다."));
    }
}
