package com.listywave.acceptance.follow;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public abstract class FollowAcceptanceTestHelper {

    public static ExtractableResponse<Response> 팔로우_요청(String accessToken, Long userId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().post("/follow/{userId}", userId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로우_취소(String accessToken, Long userId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().delete("/follow/{userId}", userId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로워_목록_조회(Long userId) {
        return given()
                .when().get("/users/{userId}/followers", userId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로워_검색(Long userId, String search) {
        return given()
                .queryParam("search", search)
                .when().get("/users/{userId}/followers", userId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로잉_목록_조회(Long userId) {
        return given()
                .when().get("/users/{userId}/followings", userId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로잉_검색(Long userId, String search) {
        return given()
                .queryParam("search", search)
                .when().get("/users/{userId}/followings", userId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로워_삭제(String accessToken, Long followerId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().delete("/followers/{userId}", followerId)
                .then().log().all()
                .extract();
    }
}
