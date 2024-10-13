package com.listywave.list.application.domain.category;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.listywave.common.exception.CustomException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {

    ENTIRE("0", "전체"),
    MUSIC("1", "음악"),
    MOVIE_DRAMA("2", "영화&드라마"),
    ENTERTAINMENT_ARTS("3","엔터&예술"),
    TRAVEL("4", "여행"),
    RESTAURANT_CAFE("5", "맛집&카페"),
    FOOD_RECIPES("6", "음식&레시피"),
    PLACE("7", "공간"),
    DAILYLIFE_THOUGHTS("8", "일상&생각"),
    HOBBY_LEISURE("9", "취미&레저"),
    ETC("10", "기타")
    ;

    private final String code;
    private final String viewName;

    public static CategoryType codeOf(String code) {
        return Arrays.stream(CategoryType.values())
                .filter(t -> t.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "해당 카테고리코드는 존재하지 않습니다."));
    }

    @JsonCreator
    public static CategoryType nameOf(String name) {
        return Arrays.stream(CategoryType.values())
                .filter(categoryType -> categoryType.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND, "해당 카테고리는 존재하지 않습니다."));
    }
}
