package com.listywave.acceptance.category;

import static com.listywave.acceptance.common.CommonAcceptanceSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.dto.response.CategoryTypeResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 관련 인수 테스트")
public class CategoryAcceptanceTest extends AcceptanceTest {

    @Test
    void 카테고리를_전체_조회한다() {
        // given
        var response = given()
                .when().get("/categories")
                .then().log().all()
                .extract();

        // when
        List<CategoryTypeResponse> body = response.as(new TypeRef<>() {
        });

        // then
        List<CategoryTypeResponse> expect = Arrays.stream(CategoryType.values())
                .map(type -> new CategoryTypeResponse(type.getCode(), type.name().toLowerCase(), type.getViewName(), type.getImageUrl()))
                .toList();
        assertThat(body).usingRecursiveAssertion()
                .isEqualTo(expect);
    }
}
