package com.listywave.acceptance.comment;

import static com.listywave.acceptance.common.CommonAcceptanceSteps.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.list.presentation.dto.request.comment.CommentCreateRequest;
import com.listywave.list.presentation.dto.request.comment.CommentUpdateRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.IntStream;

public abstract class CommentAcceptanceTestHelper {

    public static List<CommentCreateRequest> n개의_댓글_생성_요청(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> new CommentCreateRequest((i + 1) + "번 째 댓글"))
                .toList();
    }

    public static ExtractableResponse<Response> 댓글_저장_API_호출(String accessToken, Long listId, CommentCreateRequest request) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().post("/lists/{listId}/comments", listId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_조회_API_호출(Long listId) {
        return given()
                .when().get("/lists/{listId}/comments", listId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_수정_API_호출(
            String accessToken,
            CommentUpdateRequest request,
            Long listId,
            Long commentId
    ) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().patch("/lists/{listId}/comments/{commentId}", listId, commentId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글_삭제_API_호출(String accessToken, Long listId, Long commentId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().delete("/lists/{listId}/comments/{commentId}", listId, commentId)
                .then().log().all()
                .extract();
    }
}
