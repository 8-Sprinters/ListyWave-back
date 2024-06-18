package com.listywave.acceptance.collection;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class CollectionAcceptanceTestHelper {

    public static ExtractableResponse<Response> 콜렉트_또는_콜렉트취소_API_호출(String accessToken, Long listId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().post("/lists/{listId}/collect", listId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 나의_콜렉션_조회_API_호출(String accessToken, String category) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/lists/collect?category={category}", category)
                .then().log().all()
                .extract();
    }

    public static List<ExtractableResponse<Response>> 콜렉트_취소하기(String accessToken, Long listId) {
        return IntStream.range(0, 2)
                .mapToObj(i -> 콜렉트_또는_콜렉트취소_API_호출(accessToken, listId))
                .collect(Collectors.toList());
    }
}
