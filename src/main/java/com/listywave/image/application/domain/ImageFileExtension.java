package com.listywave.image.application.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO: 정수씨 이 쪽 나중에 테스트 부탁해요~
// TODO: ImageService 쪽에서 많이 쓰이고 있어서 쉽게 리팩터링 못하겠네요..
// TODO: uploadExtension 필드 제거하고 `fromString`을 `ofName` 로 바꿔주세요!
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
