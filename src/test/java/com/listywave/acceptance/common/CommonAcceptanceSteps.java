package com.listywave.acceptance.common;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class CommonAcceptanceSteps {

    public static RequestSpecification given() {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON);
    }
}
