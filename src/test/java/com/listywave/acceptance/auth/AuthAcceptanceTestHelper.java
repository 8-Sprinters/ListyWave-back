package com.listywave.acceptance.auth;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public abstract class AuthAcceptanceTestHelper {

    public static ExtractableResponse<Response> 카카오_로그인_페이지_요청_API() {
        return given()
                .when().get("/auth/kakao")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로그인_API() {
        return given()
                .queryParam("code", "AuthCode")
                .when().get("/auth/redirect/kakao")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로그아웃_API(String accessToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().patch("/auth/kakao")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원탈퇴_API(String accessToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().delete("/withdraw")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 액세스_토큰_재발급_API(String refreshToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + refreshToken)
                .when().get("/auth/token")
                .then().log().all()
                .extract();
    }
}
