package com.listywave.acceptance.list;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static com.listywave.list.application.domain.category.CategoryType.ANIMAL_PLANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.history.application.dto.HistorySearchResponse;
import com.listywave.history.application.dto.HistorySearchResponse.HistoryItemInfo;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.BackgroundColor;
import com.listywave.list.application.domain.list.BackgroundPalette;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.list.application.dto.response.ListDetailResponse.ItemResponse;
import com.listywave.list.presentation.dto.request.ItemCreateRequest;
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import com.listywave.list.presentation.dto.request.ListUpdateRequest;
import com.listywave.user.application.domain.User;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

public abstract class ListAcceptanceTestHelper {

    public static ExtractableResponse<Response> 리스트_저장_API_호출(ListCreateRequest request, String accessToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().post("/lists")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비회원_리스트_상세_조회_API_호출(Long listId) {
        return given()
                .when().get("/lists/{listId}", listId)
                .then().log().all()
                .extract();
    }

    public static ListDetailResponse 회원용_리스트_상세_조회_API_호출(String accessToken, Long listId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/lists/{listId}", listId)
                .then().log().all()
                .extract()
                .as(ListDetailResponse.class);
    }

    public static ExtractableResponse<Response> 리스트_수정_API_호출(ListUpdateRequest request, String accessToken, Long listId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().patch("/lists/{listId}", listId)
                .then().log().all()
                .extract();
    }

    public static ListCreateRequest 가장_좋아하는_견종_TOP3_생성_요청_데이터(List<Long> collaboratorIds) {
        return new ListCreateRequest(
                ANIMAL_PLANT,
                List.of("동물", "최애 동물", "강아지"),
                collaboratorIds,
                "좋아하는 견종 TOP 3",
                "지극히 주관적인 내가 가장 좋아하는 견종 3위까지",
                true,
                BackgroundPalette.PASTEL,
                BackgroundColor.PASTEL_GREEN,
                List.of(
                        new ItemCreateRequest(1, "말티즈", "", "", ""),
                        new ItemCreateRequest(2, "불독", "", "", ""),
                        new ItemCreateRequest(3, "골든 리트리버", "", "", "")
                )
        );
    }

    public static ListUpdateRequest 아이템_순위와_라벨을_바꾼_좋아하는_견종_TOP3_요청_데이터(List<Long> collaboratorIds) {
        return new ListUpdateRequest(
                ANIMAL_PLANT,
                List.of("냐옹", "멍멍"),
                collaboratorIds,
                "좋아하는 견종 TOP 3",
                "지극히 주관적인 내가 가장 좋아하는 견종 3위까지",
                true,
                BackgroundPalette.PASTEL,
                BackgroundColor.PASTEL_GREEN,
                List.of(
                        new ItemCreateRequest(1, "불독", "", "", ""),
                        new ItemCreateRequest(2, "골든 리트리버", "", "", ""),
                        new ItemCreateRequest(3, "말티즈", "", "", "")
                )
        );
    }

    public static ListCreateRequest 좋아하는_라면_TOP3_생성_요청_데이터(List<Long> collaboratorIds) {
        return new ListCreateRequest(
                CategoryType.ETC,
                List.of("라", "면", "좋"),
                collaboratorIds,
                "좋아하는 라면 TOP 3",
                "신라면, 육개장 사발면, 김치 사발면 진리",
                true,
                BackgroundPalette.PASTEL,
                BackgroundColor.PASTEL_GREEN,
                List.of(
                        new ItemCreateRequest(1, "신라면", "", "", ""),
                        new ItemCreateRequest(2, "육개장 사발면", "", "", ""),
                        new ItemCreateRequest(3, "김치 사발면", "", "", "")
                )
        );
    }

    public static void 리스트_상세_조회를_검증한다(ListDetailResponse 결과값, ListDetailResponse 기대값) {
        assertThat(결과값).usingRecursiveComparison()
                .ignoringFieldsOfTypes(Long.class)
                .ignoringFields("createdDate", "lastUpdatedDate")
                .isEqualTo(기대값);
    }

    public static List<HistorySearchResponse> 비회원_히스토리_조회_API_호출(Long listId) {
        var response = given()
                .when().get("/lists/{listId}/histories", listId)
                .then().log().all()
                .extract();
        return response.as(new TypeRef<>() {
        });
    }

    public static void 리스트의_아이템_순위와_히스토리의_아이템_순위를_검증한다(List<ItemResponse> 리스트_아이템, List<HistoryItemInfo> 히스토리_아이템) {
        assertThat(리스트_아이템).usingRecursiveComparison()
                .comparingOnlyFields("rank", "title")
                .isEqualTo(히스토리_아이템);
    }

    public static ExtractableResponse<Response> 리스트_삭제_요청_API_호출(String accessToken, Long listId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().delete("/lists/{listId}", listId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비회원_피드_리스트_조회(User user) {
        return given()
                .when().get("/users/{userId}/lists", user.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_피드_리스트_조회(User targetUser, String accessToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/users/{userId}/lists", targetUser.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비회원이_피드_리스트_조회_카테고리_필터링_요청(User targetUser, String category) {
        return given()
                .when().get("/users/{userId}/lists?category={category}", targetUser.getId(), category)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비회원이_피드_리스트_조회_콜라보레이터_필터링_요청(User targetUser) {
        return given()
                .when().get("/users/{userId}/lists?type=collabo", targetUser.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비회원이_피드_리스트_조회_카테고리_콜라보레이터_필터링_요청(User targetUser, String category) {
        return given()
                .when().get("/users/{userId}/lists?type=collabo&category={category}", targetUser.getId(), category)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 트랜딩_리스트_조회_API_호출() {
        return given()
                .when().get("/lists/explore")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비회원_최신_리스트_10개_조회_API_호출() {
        return given()
                .when().get("/lists")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_최신_리스트_10개_조회_API_호출(String accessToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/lists")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 키워드로_검색_API_호출(String keyword) {
        return given()
                .when().get("/lists/search?keyword={keyword}", keyword)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리로_검색_API_호출(String category) {
        return given()
                .when().get("/lists/search?category={category}", category)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 카테고리와_키워드로_검색_API_호출(String category, String keyword) {
        return given()
                .when().get("/lists/search?category={category}&keyword={keyword}", category, keyword)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 검색_API_호출() {
        return given()
                .when().get("/lists/search")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 정렬기준을_포함한_검색_API_호출(String sort) {
        return given()
                .when().get("/lists/search?sort={sort}", sort)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 키워드와_정렬기준을_포함한_검색_API_호출(String keyword, String sort) {
        return given()
                .when().get("/lists/search?keyword={keyword}&sort={sort}", keyword, sort)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 콜렉트_요청_API_호출(String accessToken, Long listId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().post("/lists/{listId}/collect", listId)
                .then().log().all()
                .extract();
    }
}

