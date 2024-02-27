package com.listywave.list.application.domain.category;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.listywave.common.exception.CustomException;
import com.listywave.common.exception.ErrorCode;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {

    ENTIRE("0", "전체", "https://image.listywave.com/category/category_entire.png"),
    CULTURE("1", "문화", "https://image.listywave.com/category/category_culture.png"),
    LIFE("2", "일상생활", "https://image.listywave.com/category/category_life.png"),
    PLACE("3", "장소", "https://image.listywave.com/category/category_place.png"),
    MUSIC("4", "음악", "https://image.listywave.com/category/category_music.png"),
    MOVIE_DRAMA("5", "영화/드라마", "https://image.listywave.com/category/category_movie_drama.png"),
    BOOK("6", "도서", "https://image.listywave.com/category/category_book.png"),
    ANIMAL_PLANT("7", "동식물", "https://image.listywave.com/category/category_animal_plant.png"),
    FOOD("9", "음식", "https://image.listywave.com/category/category_etc.png"),
    ETC("8", "기타", "https://image.listywave.com/category/category_etc.png"),
    ;

    private final String code;
    private final String viewName;
    private final String imageUrl;

    public static CategoryType codeOf(String code) {
        return Arrays.stream(CategoryType.values())
                .filter(t -> t.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 카테고리코드는 존재하지 않습니다."));
    }

    @JsonCreator
    public static CategoryType nameOf(String name) {
        return Arrays.stream(CategoryType.values())
                .filter(categoryType -> categoryType.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 카테고리는 존재하지 않습니다."));
    }
}
