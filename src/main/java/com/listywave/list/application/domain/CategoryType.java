package com.listywave.list.application.domain;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {

    ENTIRE("1","ENTIRE","전체"),
    CULTURE("2","CULTURE","문화"),
    LIFE("3","LIFE","일상생활"),
    PLACE("4","PLACE","장소"),
    MUSIC("5","MUSIC","음악"),
    MOVIE_DRAMA("6","MOVIE_DRAMA","영화/드라마"),
    BOOK("7","BOOK","도서"),
    ANIMAL_PLANT("8","ANIMAL_PLANT","동식물"),
    ETC("9","ETC","기타"),
    ;

    private final String codeValue;
    private final String nameValue;
    private final String korNameValue;

    public static CategoryType enumOf(String codeValue) {
        return Arrays.stream(CategoryType.values())
                .filter( t -> t.getCodeValue().equals(codeValue))
                .findAny().orElse(null);
    }
}
