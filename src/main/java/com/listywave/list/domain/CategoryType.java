package com.listywave.list.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {

    ENTIRE("전체"),
    CULTURE("문화"),
    LIFE("일상생활"),
    PLACE("장소"),
    MUSIC("음악"),
    MOVIE_DRAMA("영화/드라마"),
    BOOK("도서"),
    ANIMAL_PLANT("동식물"),
    ETC("기타"),
    ;

    private final String name;
}
