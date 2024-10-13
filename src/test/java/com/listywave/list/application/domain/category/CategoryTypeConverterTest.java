package com.listywave.list.application.domain.category;

import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static com.listywave.list.application.domain.category.CategoryType.MOVIE_DRAMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CategoryTypeConverter는 ")
class CategoryTypeConverterTest {

    private final CategoryTypeConverter categoryTypeConverter = new CategoryTypeConverter();

    @Test
    void CategoryType을_code로_변경한다() {
        String result = categoryTypeConverter.convertToDatabaseColumn(MOVIE_DRAMA);

        assertThat(result).isEqualTo(MOVIE_DRAMA.getCode());
    }

    @Test
    void DB에_저장된_code값으로_CategoryType을_변환한다() {
        String code = MOVIE_DRAMA.getCode();

        CategoryType result = categoryTypeConverter.convertToEntityAttribute(code);

        assertThat(result).isEqualTo(CategoryType.codeOf(code));
    }

    @Test
    void 존재하지_않는_code일_경우_예외가_발생한다() {
        String code = "-1";

        CustomException exception = assertThrows(CustomException.class, () -> categoryTypeConverter.convertToEntityAttribute(code));

        assertThat(exception.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
    }
}
