package com.listywave.acceptance.collection;

import com.listywave.collection.presentation.dto.FolderSelectionRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public abstract class CollectionAcceptanceTestHelper {

    public static ExtractableResponse<Response> 콜렉트_또는_콜렉트취소_API_호출(String accessToken, Long listId, FolderSelectionRequest request) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().post("/lists/{listId}/collect", listId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 나의_콜렉션_조회_API_호출(String accessToken, Long folderId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/folder/{folderId}/collections", folderId)
                .then().log().all()
                .extract();
    }
}
