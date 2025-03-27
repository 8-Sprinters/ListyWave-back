package com.listywave.list.application.domain.category;

import static com.listywave.list.application.domain.category.CategoryType.MOVIE_DRAMA;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CategoryTypeTest {

    @ParameterizedTest
    @CsvSource(value = {"0, ENTIRE"})
    void 코드값으로_Enum을_가져온다() {
        // given
        int code = 2;

        // when
        CategoryType result = CategoryType.codeOf(String.valueOf(code));

        // then
        assertThat(result).isEqualTo(MOVIE_DRAMA);
    }

    @Test
    void 대소문자_구분없이_이름으로_Enum을_가져온다() {
        // given
        String korName = "movie_drama";

        // when
        CategoryType result1 = CategoryType.nameOf(korName);
        CategoryType result2 = CategoryType.nameOf(korName.toUpperCase(ENGLISH));

        // then
        assertThat(result1).isEqualTo(MOVIE_DRAMA);
        assertThat(result2).isEqualTo(MOVIE_DRAMA);
    }
}
