package com.listywave.acceptance.user;

import static com.listywave.acceptance.common.CommonAcceptanceSteps.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public abstract class UserAcceptanceTestHelper {

    public static ExtractableResponse<Response> 회원_정보_조회_요청(Long id) {
        return given()
                .when().get("/users/{userId}", id)
                .then().log().all()
                .extract();
    }
}
