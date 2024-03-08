package com.listywave.acceptance.reply;

import static com.listywave.acceptance.common.CommonAcceptanceSteps.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.list.presentation.dto.request.ReplyCreateRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public abstract class ReplyAcceptanceTestHelper {

    public static ExtractableResponse<Response> 답글_등록_API_호출(
            String accessToken,
            ReplyCreateRequest request,
            Long listId,
            Long commentId
    ) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().post("/lists/{listId}/comments/{commentId}/replies", listId, commentId)
                .then().log().all()
                .extract();
    }
}
