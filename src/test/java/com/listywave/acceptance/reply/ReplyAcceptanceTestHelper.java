package com.listywave.acceptance.reply;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.list.presentation.dto.request.ReplyCreateRequest;
import com.listywave.list.presentation.dto.request.ReplyUpdateRequest;
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

    public static ExtractableResponse<Response> 답글_수정_API_호출(
            String accessToken,
            ReplyUpdateRequest request,
            Long listId,
            Long commentId,
            Long replyId
    ) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().patch("/lists/{listId}/comments/{commentId}/replies/{replyId}", listId, commentId, replyId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 답글_삭제_API_호출(
            String accessToken,
            Long listId,
            Long commentId,
            Long replyId
    ) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().delete("/lists/{listId}/comments/{commentId}/replies/{replyId}", listId, commentId, replyId)
                .then().log().all()
                .extract();
    }
}
