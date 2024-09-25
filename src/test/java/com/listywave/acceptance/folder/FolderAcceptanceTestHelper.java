package com.listywave.acceptance.folder;

import com.listywave.collection.presentation.dto.FolderCreateRequest;
import com.listywave.collection.presentation.dto.FolderSelectionRequest;
import com.listywave.collection.presentation.dto.FolderUpdateRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public abstract class FolderAcceptanceTestHelper {

    public static ExtractableResponse<Response> 폴더_생성_API_호출(String accessToken, FolderCreateRequest request) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().post("/folder")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 폴더_수정_API_호출(String accessToken, FolderUpdateRequest request, Long folderId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .body(request)
                .when().put("/folder/{folderId}", folderId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 폴더_삭제_API_호출(String accessToken, Long folderId) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().delete("/folder/{folderId}", folderId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 폴더_조회_API_호출(String accessToken) {
        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().get("/folder")
                .then().log().all()
                .extract();
    }

    public static FolderCreateRequest 폴더_생성_요청_데이터(String folderName) {
        return new FolderCreateRequest(folderName);
    }

    public static FolderSelectionRequest 폴더_선택_요청_데이터(Long folderId) {
        return new FolderSelectionRequest(folderId);
    }

    public static FolderUpdateRequest 폴더_수정_요청_데이터(String folderName) {
        return new FolderUpdateRequest(folderName);
    }
}
