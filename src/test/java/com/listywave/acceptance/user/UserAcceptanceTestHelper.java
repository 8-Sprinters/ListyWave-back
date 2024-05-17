package com.listywave.acceptance.user;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.user.presentation.dto.UserProfileUpdateRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.lang.Nullable;

public abstract class UserAcceptanceTestHelper {

    public static ExtractableResponse<Response> 비회원_회원_정보_조회_요청(Long userId) {
        return given()
                .when().get("/users/{userId}", userId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원이_회원_정보_조회_요청(String accessToken, Long userId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/users/{userId}", userId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비회원이_사용자_검색() {
        return given()
                .when().get("/users")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 비회원이_사용자_검색(String keyword) {
        return given()
                .queryParam("search", keyword)
                .when().get("/users")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원이_사용자_검색(String accessToken, String keyword) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .queryParam("search", keyword)
                .when().get("/users")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 닉네임_중복_체크_요청(String nickname) {
        return given()
                .queryParam("nickname", nickname)
                .when().get("/users/exists")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 추천_사용자_조회(String accessToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/users/recommend")
                .then().log().all()
                .extract();
    }

    public static UserProfileUpdateRequest 프로필_수정_요청_데이터(
            String nickname,
            @Nullable String description,
            @Nullable String profileImageUrl,
            @Nullable String backgroundImageUrl
    ) {
        return new UserProfileUpdateRequest(nickname, description, profileImageUrl, backgroundImageUrl);
    }

    public static ExtractableResponse<Response> 프로필_수정_요청(
            String accessToken,
            Long userId,
            UserProfileUpdateRequest request
    ) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().patch("/users/{userId}", userId)
                .then().log().all()
                .extract();
    }
}
