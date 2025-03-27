package com.listywave.acceptance.reaction;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.reaction.application.domain.Reaction;
import com.listywave.reaction.presentation.dto.request.ReactionRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ReactionAcceptanceTestHelper {

    public static ExtractableResponse<Response> 리액션_API_호출(String accessToken, Long listId, ReactionRequest request) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().post("/lists/{listId}/reaction", listId)
                .then().log().all()
                .extract();
    }

    public static List<ReactionRequest> 리액션_요청_데이터_리스트(Reaction... reactions) {
        return Arrays.stream(reactions)
                .map(ReactionRequest::new)
                .collect(Collectors.toList());
    }

    public static void 리액션_일괄_호출(String accessToken, Long listId, List<ReactionRequest> reactionRequests) {
        reactionRequests.forEach(reaction -> 리액션_API_호출(accessToken, listId, reaction));
    }
}
