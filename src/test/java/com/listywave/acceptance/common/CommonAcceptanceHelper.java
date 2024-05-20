package com.listywave.acceptance.common;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;

public abstract class CommonAcceptanceHelper {

    public static RequestSpecification given() {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON);
    }

    public static void HTTP_상태_코드를_검증한다(ExtractableResponse<Response> 응답, HttpStatus 기대값) {
        assertThat(응답.statusCode()).isEqualTo(기대값.value());
    }
}
