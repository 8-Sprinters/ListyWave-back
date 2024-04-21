package com.listywave.acceptance.alarm;

import static com.listywave.acceptance.common.CommonAcceptanceSteps.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public abstract class AlarmAcceptanceTestHelper {

    public static ExtractableResponse<Response> 알람_조회_API_호출(String accessToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/alarms")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 알람_읽기_API_호출(String accessToken, Long alarmId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().patch("/alarms/{alarmId}", alarmId)
                .then().log().all()
                .extract();
    }
}
