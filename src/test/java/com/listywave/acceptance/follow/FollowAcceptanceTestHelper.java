package com.listywave.acceptance.follow;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public abstract class FollowAcceptanceTestHelper {

    public static ExtractableResponse<Response> 팔로우_요청_API(String 팔로우를_하는_유저의_액세스토큰, Long 팔로우_대상_유저_ID) {
        return given()
                .header(AUTHORIZATION, "Bearer " + 팔로우를_하는_유저의_액세스토큰)
                .when().post("/follow/{userId}", 팔로우_대상_유저_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로우_취소_API(String 팔로우_취소를_하는_유저의_액세스토큰, Long 팔로우_취소_대상_유저_ID) {
        return given()
                .header(AUTHORIZATION, "Bearer " + 팔로우_취소를_하는_유저의_액세스토큰)
                .when().delete("/follow/{userId}", 팔로우_취소_대상_유저_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로워_목록_조회_API(Long 조회하려는_유저_ID) {
        return given()
                .when().get("/users/{userId}/followers", 조회하려는_유저_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로워_검색_API(Long 검색하려는_유저_ID, String 검색어) {
        return given()
                .queryParam("search", 검색어)
                .when().get("/users/{userId}/followers", 검색하려는_유저_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로잉_목록_조회_API(Long 조회하려는_유저_ID) {
        return given()
                .when().get("/users/{userId}/followings", 조회하려는_유저_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로잉_검색_API(Long 검색하려는_유저_ID, String 검색어) {
        return given()
                .queryParam("search", 검색어)
                .when().get("/users/{userId}/followings", 검색하려는_유저_ID)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 팔로워_삭제_API(String 삭제를_수행하려는_유저의_액세스토큰, Long 삭제의_대상이_되는_유저의_ID) {
        return given()
                .header(AUTHORIZATION, "Bearer " + 삭제를_수행하려는_유저의_액세스토큰)
                .when().delete("/followers/{userId}", 삭제의_대상이_되는_유저의_ID)
                .then().log().all()
                .extract();
    }
}
