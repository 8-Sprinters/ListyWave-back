package com.listywave.acceptance.list;

import static com.listywave.acceptance.common.CommonAcceptanceSteps.given;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.검색_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트_삭제_요청_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트_상세_조회를_검증한다;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트_수정_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트_저장_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트의_아이템_순위와_히스토리의_아이템_순위를_검증한다;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.비회원_리스트_상세_조회_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.비회원_최신_리스트_10개_조회_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.비회원_피드_리스트_조회;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.비회원_히스토리_조회_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.비회원이_피드_리스트_조회_카테고리_콜라보레이터_필터링_요청;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.비회원이_피드_리스트_조회_카테고리_필터링_요청;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.비회원이_피드_리스트_조회_콜라보레이터_필터링_요청;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.아이템_순위와_라벨이_바뀐_좋아하는_견종_TOP3_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.정렬기준을_포함한_검색_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.좋아하는_견종_TOP3_생성_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.좋아하는_라면_TOP3_생성_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.카테고리로_검색_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.카테고리와_키워드로_검색_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.콜렉트_요청_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.키워드로_검색_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.키워드와_정렬기준을_포함한_검색_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.트랜딩_리스트_조회_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.회원_최신_리스트_10개_조회_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.회원_피드_리스트_조회;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.회원용_리스트_상세_조회_API_호출;
import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3_순위_변경;
import static com.listywave.list.fixture.ListFixture.좋아하는_라면_TOP3;
import static com.listywave.list.fixture.ListFixture.지정된_개수만큼_리스트를_생성한다;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.유진;
import static com.listywave.user.fixture.UserFixture.정수;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.auth.infra.kakao.response.KakaoLogoutResponse;
import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.history.application.dto.HistorySearchResponse;
import com.listywave.history.application.dto.HistorySearchResponse.HistoryItemInfo;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.list.application.dto.response.ListDetailResponse.ItemResponse;
import com.listywave.list.application.dto.response.ListRecentResponse;
import com.listywave.list.application.dto.response.ListSearchResponse;
import com.listywave.list.application.dto.response.ListTrandingResponse;
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import com.listywave.list.presentation.dto.request.ListUpdateRequest;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.FindFeedListResponse;
import com.listywave.user.application.dto.FindFeedListResponse.FeedListInfo;
import com.listywave.user.application.dto.FindFeedListResponse.ListItemsResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("리스트 관련 인수테스트")
public class ListAcceptanceTest extends AcceptanceTest {

    @Nested
    class 리스트_생성 {

        @Test
        void 리스트를_성공적으로_생성한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String accessToken = 액세스_토큰을_발급한다(동호);
            ListCreateRequest listCreateRequest = 좋아하는_견종_TOP3_생성_요청_데이터(List.of());

            // when
            ExtractableResponse<Response> response = 리스트_저장_API_호출(listCreateRequest, accessToken);
            ListCreateResponse result = response.as(ListCreateResponse.class);

            // then
            assertThat(response.statusCode()).isEqualTo(CREATED.value());
            assertThat(result.listId()).isEqualTo(1L);
        }

        @Test
        void 콜라보레이터를_지정해_리스트를_생성할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String accessToken = 액세스_토큰을_발급한다(동호);
            ListCreateRequest listCreateRequest = 좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId()));

            // when
            ExtractableResponse<Response> response = 리스트_저장_API_호출(listCreateRequest, accessToken);
            ListCreateResponse result = response.as(ListCreateResponse.class);

            // then
            assertThat(result.listId()).isEqualTo(1L);
            ListDetailResponse list = 비회원_리스트_상세_조회_API_호출(result.listId()).as(ListDetailResponse.class);
            assertThat(list.collaborators().get(0).id()).isEqualTo(정수.getId());
        }

        @Test
        void 인증_정보가_없으면_401_에러가_발생한다() {
            // given
            ListCreateRequest listCreateRequest = 좋아하는_견종_TOP3_생성_요청_데이터(List.of());

            // when
            ExtractableResponse<Response> response = given()
                    .body(listCreateRequest)
                    .when().post("/lists")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED.value());
        }

        @Test
        void 리스트_생성_시_히스토리도_함께_생성된다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String accessToken = 액세스_토큰을_발급한다(동호);
            ListCreateRequest listCreateRequest = 좋아하는_견종_TOP3_생성_요청_데이터(List.of());

            ListCreateResponse 리스트_저장_API_응답 = 리스트_저장_API_호출(listCreateRequest, accessToken).as(ListCreateResponse.class);
            Long 생성된_리스트_ID = 리스트_저장_API_응답.listId();

            // when
            List<HistorySearchResponse> 비회원_히스토리_조회_API_응답 = 비회원_히스토리_조회_API_호출(생성된_리스트_ID);

            // then
            HistorySearchResponse 첫_히스토리 = 비회원_히스토리_조회_API_응답.get(0);
            assertThat(첫_히스토리.isPublic()).isTrue();
            assertThat(첫_히스토리.items()).usingRecursiveComparison()
                    .comparingOnlyFields("rank", "title")
                    .isEqualTo(listCreateRequest.items());
        }
    }

    @Nested
    class 리스트_상세_조회 {

        @Test
        void 비회원이_리스트를_상세_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            // when
            ExtractableResponse<Response> response = 비회원_리스트_상세_조회_API_호출(동호_리스트.getId());
            ListDetailResponse result = response.as(ListDetailResponse.class);

            // then
            ListDetailResponse expect = ListDetailResponse.of(동호_리스트, 동호, false, List.of());
            리스트_상세_조회를_검증한다(result, expect);
        }

        @Test
        void 회원이_리스트를_상세_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of(동호.getId(), 정수.getId())));
            콜렉트를_저장한다(동호, 동호_리스트);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);

            // when
            ListDetailResponse result = 회원용_리스트_상세_조회_API_호출(정수_액세스_토큰, 동호_리스트.getId());

            // then
            ListDetailResponse expect = ListDetailResponse.of(
                    동호_리스트,
                    동호,
                    collectionRepository.existsByListAndUserId(동호_리스트, 정수.getId()),
                    collaboratorRepository.findAllByList(동호_리스트)
            );
            리스트_상세_조회를_검증한다(result, expect);
        }

        @Test
        void 로그인을_한_상태로_본인이_작성한_리스트를_상세_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of(동호.getId(), 정수.getId())));
            콜렉트를_저장한다(정수, 동호_리스트);
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            ListDetailResponse result = 회원용_리스트_상세_조회_API_호출(동호_액세스_토큰, 동호_리스트.getId());

            // then
            ListDetailResponse expect = ListDetailResponse.of(
                    동호_리스트,
                    동호,
                    false,
                    collaboratorRepository.findAllByList(동호_리스트)
            );
            리스트_상세_조회를_검증한다(result, expect);
        }

        @Test
        void 순위가_변경된_리스트를_상세_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            ListUpdateRequest 리스트_수정_요청_데이터 = 아이템_순위와_라벨이_바뀐_좋아하는_견종_TOP3_요청_데이터(List.of());
            리스트_수정_API_호출(리스트_수정_요청_데이터, 동호_액세스_토큰, 동호_리스트.getId());

            // when
            ExtractableResponse<Response> response = 비회원_리스트_상세_조회_API_호출(동호_리스트.getId());
            ListDetailResponse result = response.as(ListDetailResponse.class);

            // then
            ListEntity 바뀐_리스트 = 가장_좋아하는_견종_TOP3_순위_변경(동호, List.of());
            ListDetailResponse expect = ListDetailResponse.of(바뀐_리스트, 동호, false, List.of());
            리스트_상세_조회를_검증한다(result, expect);
        }

        @Test
        void 탈퇴한_회원의_리스트는_볼_수_없다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            회원_탈퇴(new KakaoLogoutResponse(1L), 동호_액세스_토큰);

            // when
            ExtractableResponse<Response> response = 비회원_리스트_상세_조회_API_호출(동호_리스트.getId());

            // then
            assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        }
    }

    @Nested
    class 리스트_수정 {

        @Test
        void 리스트를_성공적으로_수정한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            User 유진 = 회원을_저장한다(유진());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListCreateResponse 동호_리스트_생성_결과 = 리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            ListUpdateRequest 리스트_수정_요청_데이터 = 아이템_순위와_라벨이_바뀐_좋아하는_견종_TOP3_요청_데이터(List.of(유진.getId()));
            리스트_수정_API_호출(리스트_수정_요청_데이터, 동호_액세스_토큰, 동호_리스트_생성_결과.listId());

            // then
            ListDetailResponse result = 회원용_리스트_상세_조회_API_호출(동호_액세스_토큰, 동호_리스트_생성_결과.listId());
            ListEntity 수정된_리스트 = 가장_좋아하는_견종_TOP3_순위_변경(동호, List.of());
            ListDetailResponse expect = ListDetailResponse.of(수정된_리스트, 동호, false, List.of(Collaborator.init(유진, 수정된_리스트)));
            리스트_상세_조회를_검증한다(result, expect);
        }

        @Test
        void 아이템_순위에_변동이_있으면_히스토리로_기록된다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            ListUpdateRequest 리스트_수정_요청_데이터 = 아이템_순위와_라벨이_바뀐_좋아하는_견종_TOP3_요청_데이터(List.of());
            리스트_수정_API_호출(리스트_수정_요청_데이터, 동호_액세스_토큰, 동호_리스트.getId());

            // when
            List<HistorySearchResponse> 히스토리_조회_결과 = 비회원_히스토리_조회_API_호출(동호_리스트.getId());
            ListDetailResponse 수정된_리스트_상세_조회_결과 = 비회원_리스트_상세_조회_API_호출(동호_리스트.getId()).as(ListDetailResponse.class);

            // then
            List<ItemResponse> 수정된_리스트의_아이템_정보들 = 수정된_리스트_상세_조회_결과.items();
            List<HistoryItemInfo> 히스토리의_아이템_정보들 = 히스토리_조회_결과.get(0).items();
            assertAll(
                    () -> assertThat(히스토리_조회_결과.size()).isOne(),
                    () -> assertThat(히스토리_조회_결과.get(0).createdDate()).isEqualTo(수정된_리스트_상세_조회_결과.lastUpdatedDate()),
                    () -> 리스트의_아이템_순위와_히스토리의_아이템_순위를_검증한다(수정된_리스트의_아이템_정보들, 히스토리의_아이템_정보들)
            );
        }

        @Test
        void 리스트의_작성자와_콜라보레이터만_수정할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            User 유진 = 회원을_저장한다(유진());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            String 유진_액세스_토큰 = 액세스_토큰을_발급한다(유진);
            ListCreateResponse 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            ListUpdateRequest 수정_요청_데이터 = 아이템_순위와_라벨이_바뀐_좋아하는_견종_TOP3_요청_데이터(List.of(정수.getId()));
            ExtractableResponse<Response> 동호가_보낸_리스트_수정_API = 리스트_수정_API_호출(수정_요청_데이터, 동호_액세스_토큰, 좋아하는_견종_TOP3_생성_결과.listId());
            ExtractableResponse<Response> 정수가_보낸_리스트_수정_API = 리스트_수정_API_호출(수정_요청_데이터, 정수_액세스_토큰, 좋아하는_견종_TOP3_생성_결과.listId());
            ExtractableResponse<Response> 유진이_보낸_리스트_수정_API = 리스트_수정_API_호출(수정_요청_데이터, 유진_액세스_토큰, 좋아하는_견종_TOP3_생성_결과.listId());

            // then
            assertThat(동호가_보낸_리스트_수정_API.statusCode()).isEqualTo(NO_CONTENT.value());
            assertThat(정수가_보낸_리스트_수정_API.statusCode()).isEqualTo(NO_CONTENT.value());
            assertThat(유진이_보낸_리스트_수정_API.statusCode()).isEqualTo(FORBIDDEN.value());
        }
    }

    @Nested
    class 리스트_삭제 {

        @Test
        void 인증_정보가_없으면_리스트_삭제에_실패한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));

            // when
            ExtractableResponse<Response> result = 리스트_삭제_요청_API_호출(null, 동호_리스트);

            // then
            assertThat(result.statusCode()).isEqualTo(UNAUTHORIZED.value());
        }

        @Test
        void 타인의_리스트를_삭제하는_경우_실패한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);

            // when
            ExtractableResponse<Response> result = 리스트_삭제_요청_API_호출(정수_액세스_토큰, 동호_리스트);

            // then
            assertThat(result.statusCode()).isEqualTo(FORBIDDEN.value());
        }

        @Test
        void 리스트를_삭제한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            ListEntity 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            ExtractableResponse<Response> result = 리스트_삭제_요청_API_호출(동호_액세스_토큰, 동호_리스트);

            // then
            assertThat(result.statusCode()).isEqualTo(NO_CONTENT.value());
            assertThat(비회원_리스트_상세_조회_API_호출(동호_리스트.getId()).statusCode()).isEqualTo(NOT_FOUND.value());
        }
    }

    @Nested
    class 피드_리스트_조회 {

        @Test
        void 비회원이_피드_리스트를_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            ListEntity 동호_리스트_1 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));
            ListEntity 동호_리스트_2 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));

            // when
            FindFeedListResponse result = 비회원_피드_리스트_조회(동호).as(FindFeedListResponse.class);

            // then
            assertThat(result.hasNext()).isFalse();
            List<FeedListInfo> expect = List.of(FeedListInfo.of(동호_리스트_2), FeedListInfo.of(동호_리스트_1));
            assertThat(result.feedLists()).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expect);
        }

        @Test
        void 회원이_피드_리스트를_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));
            ListEntity 동호_리스트_2 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_3 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_4 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_5 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_6 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_7 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_8 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_9 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_10 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            ListEntity 동호_리스트_11 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);

            // when
            FindFeedListResponse allUserListsResponse = 회원_피드_리스트_조회(동호, 정수_액세스_토큰).as(FindFeedListResponse.class);

            // then
            assertAll(
                    () -> assertThat(allUserListsResponse.cursorUpdatedDate()).isCloseTo(동호_리스트_2.getUpdatedDate(), within(1, MILLIS)),
                    () -> assertThat(allUserListsResponse.hasNext()).isTrue(),
                    () -> assertThat(allUserListsResponse.feedLists()).usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(List.of(
                                    FeedListInfo.of(동호_리스트_11),
                                    FeedListInfo.of(동호_리스트_10),
                                    FeedListInfo.of(동호_리스트_9),
                                    FeedListInfo.of(동호_리스트_8),
                                    FeedListInfo.of(동호_리스트_7),
                                    FeedListInfo.of(동호_리스트_6),
                                    FeedListInfo.of(동호_리스트_5),
                                    FeedListInfo.of(동호_리스트_4),
                                    FeedListInfo.of(동호_리스트_3),
                                    FeedListInfo.of(동호_리스트_2)
                            ))
            );
        }

        @Test
        void 카테고리로_필터링한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            List<ListEntity> 동호_리스트들 = 지정된_개수만큼_리스트를_생성한다(동호, 11);
            리스트를_모두_저장한다(동호_리스트들);

            // when
            FindFeedListResponse result = 비회원이_피드_리스트_조회_카테고리_필터링_요청(동호, "book").as(FindFeedListResponse.class);

            // then
            CategoryType 필터링_조건 = CategoryType.nameOf("book");
            List<FeedListInfo> expect = 동호_리스트들.stream()
                    .sorted(comparing(ListEntity::getId, reverseOrder()))
                    .filter(list -> list.isCategoryType(필터링_조건))
                    .map(FeedListInfo::of)
                    .toList();

            assertThat(result.feedLists()).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expect);
        }

        @Test
        void 콜라보레이터로_필터링한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            Long 리스트_1_ID = 리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰).as(ListCreateResponse.class).listId();
            Long 리스트_2_ID = 리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of(동호.getId())), 정수_액세스_토큰).as(ListCreateResponse.class).listId();

            // when
            FindFeedListResponse result = 비회원이_피드_리스트_조회_콜라보레이터_필터링_요청(동호).as(FindFeedListResponse.class);

            // then
            assertThat(result.feedLists()).hasSize(2);
            assertThat(result.feedLists().get(0).id()).isEqualTo(리스트_2_ID);
            assertThat(result.feedLists().get(1).id()).isEqualTo(리스트_1_ID);
        }

        @Test
        void 콜라보레이터와_카테고리로_필터링한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            ListCreateResponse 동호_리스트_2 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            FindFeedListResponse result = 비회원이_피드_리스트_조회_카테고리_콜라보레이터_필터링_요청(동호, "etc").as(FindFeedListResponse.class);

            // then
            assertThat(result.feedLists()).hasSize(1);
            assertThat(result.feedLists().get(0).id()).isEqualTo(동호_리스트_2.listId());
        }
    }

    @Nested
    class 리스트_탐색 {

        @Test
        void 트랜딩_리스트를_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(정수, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));

            // when
            ExtractableResponse<Response> response = 트랜딩_리스트_조회_API_호출();
            List<ListTrandingResponse> result = response.as(new TypeRef<>() {
            });

            // then
            List<FeedListInfo> 동호_리스트 = 비회원_피드_리스트_조회(동호).as(FindFeedListResponse.class).feedLists();
            List<FeedListInfo> 정수_리스트 = 비회원_피드_리스트_조회(정수).as(FindFeedListResponse.class).feedLists();
            List<FeedListInfo> 모든_리스트 = new ArrayList<>(동호_리스트);
            모든_리스트.addAll(정수_리스트);

            List<Long> expect = 모든_리스트.stream()
                    .sorted(comparing(FeedListInfo::id, reverseOrder()))
                    .map(FeedListInfo::id)
                    .limit(10)
                    .toList();
            assertThat(result.stream().map(ListTrandingResponse::id)).isEqualTo(expect);

            List<String> 대표_이미지들 = 모든_리스트.stream()
                    .sorted(comparing(FeedListInfo::id, reverseOrder()))
                    .map(feedListInfo -> feedListInfo.listItems().stream()
                            .sorted(comparing(ListItemsResponse::rank))
                            .filter(listItemsResponse -> listItemsResponse.imageUrl() != null && !listItemsResponse.imageUrl().isBlank())
                            .map(ListItemsResponse::imageUrl)
                            .findFirst()
                            .orElse(""))
                    .toList();
            assertThat(result).usingRecursiveComparison()
                    .comparingOnlyFields("itemImageUrl")
                    .isEqualTo(대표_이미지들);
        }

        @Test
        void 회원이_최신_리스트_10개를_조회하면_팔로우한_사용자의_최신_리스트_10개가_반환된다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            팔로우를_저장한다(동호, 정수);
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(정수, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            ExtractableResponse<Response> response = 회원_최신_리스트_10개_조회_API_호출(동호_액세스_토큰);
            ListRecentResponse result = response.as(ListRecentResponse.class);

            // then
            assertThat(result.lists()).hasSize(5);
        }

        @Test
        void 비회원이_최신_리스트_10개를_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            팔로우를_저장한다(동호, 정수);
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(정수, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));

            // when
            ExtractableResponse<Response> response = 비회원_최신_리스트_10개_조회_API_호출();
            ListRecentResponse result = response.as(ListRecentResponse.class);

            // then
            List<FeedListInfo> 동호_리스트 = 비회원_피드_리스트_조회(동호).as(FindFeedListResponse.class).feedLists();
            List<FeedListInfo> 정수_리스트 = 비회원_피드_리스트_조회(정수).as(FindFeedListResponse.class).feedLists();
            List<FeedListInfo> 모든_리스트 = new ArrayList<>(동호_리스트);
            모든_리스트.addAll(정수_리스트);
            List<Long> expect = 모든_리스트.stream()
                    .sorted(comparing(FeedListInfo::id, reverseOrder()))
                    .filter(FeedListInfo::isPublic)
                    .map(FeedListInfo::id)
                    .limit(10)
                    .toList();

            assertAll(
                    () -> assertThat(result.lists()).hasSize(10),
                    () -> assertThat(result.lists()).usingRecursiveComparison()
                            .comparingOnlyFields("id")
                            .isEqualTo(expect),
                    () -> assertThat(result.lists()).allMatch(listResponse -> listResponse.items().size() == 3)
            );
        }
    }

    @Nested
    class 리스트_검색 {

        @Test
        void 키워드로_검색할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰);
            ListCreateResponse 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            ExtractableResponse<Response> response = 키워드로_검색_API_호출("라면");
            ListSearchResponse result = response.as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(result.totalCount()).isOne(),
                    () -> assertThat(result.resultLists()).hasSize(1),
                    () -> assertThat(result.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId())
            );
        }

        @Test
        void 카테고리로_필터링_할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰);
            ListCreateResponse 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            ExtractableResponse<Response> response = 카테고리로_검색_API_호출("etc");
            ListSearchResponse result = response.as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(result.totalCount()).isOne(),
                    () -> assertThat(result.resultLists()).hasSize(1),
                    () -> assertThat(result.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId())
            );
        }

        @Test
        void 카테고리와_키워드로_검색할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListCreateResponse 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            ExtractableResponse<Response> response = 카테고리와_키워드로_검색_API_호출("animal_plant", "견종");
            ListSearchResponse result = response.as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(result.totalCount()).isOne(),
                    () -> assertThat(result.resultLists()).hasSize(1),
                    () -> assertThat(result.resultLists().get(0).id()).isEqualTo(좋아하는_견종_TOP3_생성_결과.listId())
            );
        }

        @Test
        void 최신순으로_정렬할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListCreateResponse 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            ListCreateResponse 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            ExtractableResponse<Response> response = 검색_API_호출();
            ListSearchResponse result = response.as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(result.totalCount()).isEqualTo(2),
                    () -> assertThat(result.resultLists()).hasSize(2),
                    () -> assertThat(result.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId()),
                    () -> assertThat(result.resultLists().get(1).id()).isEqualTo(좋아하는_견종_TOP3_생성_결과.listId())
            );
        }

        @Test
        void 오래된순으로_정렬할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            ListCreateResponse 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            ListCreateResponse 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            ExtractableResponse<Response> response = 정렬기준을_포함한_검색_API_호출("old");
            ListSearchResponse result = response.as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(result.totalCount()).isEqualTo(2),
                    () -> assertThat(result.resultLists()).hasSize(2),
                    () -> assertThat(result.resultLists().get(0).id()).isEqualTo(좋아하는_견종_TOP3_생성_결과.listId()),
                    () -> assertThat(result.resultLists().get(1).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId())
            );
        }

        @Test
        void 콜렉트순으로_정렬할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            ListCreateResponse 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            ListCreateResponse 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            콜렉트_요청_API_호출(정수_액세스_토큰, 좋아하는_라면_TOP3_생성_결과.listId());

            // when
            ExtractableResponse<Response> response = 정렬기준을_포함한_검색_API_호출("collect");
            ListSearchResponse result = response.as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(result.totalCount()).isEqualTo(2),
                    () -> assertThat(result.resultLists()).hasSize(2),
                    () -> assertThat(result.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId()),
                    () -> assertThat(result.resultLists().get(1).id()).isEqualTo(좋아하는_견종_TOP3_생성_결과.listId())
            );
        }

        // TODO: 테스트셋 보충 필요
        @Test
        void 관련도순으로_정렬할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            리스트_저장_API_호출(좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            ListCreateResponse 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            ExtractableResponse<Response> response = 키워드와_정렬기준을_포함한_검색_API_호출("라면", "related");
            ListSearchResponse result = response.as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(result.totalCount()).isOne(),
                    () -> assertThat(result.resultLists()).hasSize(1),
                    () -> assertThat(result.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId())
            );
        }
    }
}
