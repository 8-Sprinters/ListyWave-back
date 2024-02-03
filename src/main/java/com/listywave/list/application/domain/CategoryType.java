package com.listywave.list.application.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {

    ENTIRE("0","전체"),
    CULTURE("1","문화"),
    LIFE("2","일상생활"),
    PLACE("3","장소"),
    MUSIC("4","음악"),
    MOVIE_DRAMA("5","영화/드라마"),
    BOOK("6","도서"),
    ANIMAL_PLANT("7","동식물"),
    ETC("8","기타"),
    ;

    private final String codeValue;
    private final String korNameValue;

    public static CategoryType enumOf(String codeValue) {
        return Arrays.stream(CategoryType.values())
                .filter( t -> t.getCodeValue().equals(codeValue))
                .findAny().orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 카테고리코드는 존재하지 않습니다."));
    }

    @JsonCreator
    public static CategoryType fromString(String key) {
        for(CategoryType category : CategoryType.values()) {
            if(category.name().equalsIgnoreCase(key)) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND, "해당 카테고리는 존재하지 않습니다.");
    }


}
