package com.listywave.category;

import java.util.Arrays;

public enum Category {

    MOVIE("무비"),
    FOOD("음식"),
    ANIMAL_AND_PLANT("동/식물")
    ;

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public static Category of(String filter) {
        return Arrays.stream(Category.values())
                .filter(it -> it.value.equalsIgnoreCase(filter))
                .findFirst()
                .orElseThrow(() -> new RuntimeException());
    }

    public String getValue() {
        return value;
    }
}
