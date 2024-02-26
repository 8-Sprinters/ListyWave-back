package com.listywave.list.application.domain.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CategoryFixture {

    public static CategoryType 무작위_카테고리_추출() {
        List<CategoryType> categoryTypes = new ArrayList<>(List.of(CategoryType.values()));
        Collections.shuffle(categoryTypes);
        return categoryTypes.get(0);
    }
}
