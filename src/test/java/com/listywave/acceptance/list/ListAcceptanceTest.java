package com.listywave.acceptance.list;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.HTTP_상태_코드를_검증한다;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로우_요청_API;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.가장_좋아하는_견종_TOP3_생성_요청_데이터;
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
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.아이템_순위와_라벨을_바꾼_좋아하는_견종_TOP3_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.정렬기준을_포함한_검색_API_호출;
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
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.list.application.dto.response.ListRecentResponse;
import com.listywave.list.application.dto.response.ListSearchResponse;
import com.listywave.list.application.dto.response.ListTrandingResponse;
import com.listywave.user.application.dto.FindFeedListResponse;
import com.listywave.user.application.dto.FindFeedListResponse.FeedListInfo;
import com.listywave.user.application.dto.FindFeedListResponse.ListItemsResponse;
import io.restassured.common.mapper.TypeRef;
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
            var 동호 = 회원을_저장한다(동호());
            var accessToken = 액세스_토큰을_발급한다(동호);
            var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());

            // when
            var 응답 = 리스트_저장_API_호출(리스트_생성_요청_데이터, accessToken);
            var 결과 = 응답.as(ListCreateResponse.class);

            // then
            HTTP_상태_코드를_검증한다(응답, CREATED);
            assertThat(결과.listId()).isEqualTo(1L);
        }

        @Test
        void 콜라보레이터를_지정해_리스트를_생성할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId()));

            // when
            var 응답 = 리스트_저장_API_호출(리스트_생성_요청_데이터, 동호_액세스_토큰);
            var 결과 = 응답.as(ListCreateResponse.class);

            var 생성된_리스트 = 비회원_리스트_상세_조회_API_호출(결과.listId()).as(ListDetailResponse.class);

            // then
            assertThat(결과.listId()).isEqualTo(1L);
            assertThat(생성된_리스트.collaborators().get(0).id()).isEqualTo(정수.getId());
        }

        @Test
        void 인증_정보가_없으면_401_에러가_발생한다() {
            // given
            var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());

            // when
            var 응답 = 리스트_저장_API_호출(리스트_생성_요청_데이터, null);

            // then
            HTTP_상태_코드를_검증한다(응답, UNAUTHORIZED);
        }

        @Test
        void 리스트_생성_시_히스토리도_함께_생성된다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());

            var 생성된_리스트_ID = 리스트_저장_API_호출(리스트_생성_요청_데이터, 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            // when
            var 응답 = 비회원_히스토리_조회_API_호출(생성된_리스트_ID);
            var 첫_히스토리 = 응답.get(0);

            // then
            assertThat(첫_히스토리.isPublic()).isTrue();
            assertThat(첫_히스토리.items()).usingRecursiveComparison()
                    .comparingOnlyFields("rank", "title")
                    .isEqualTo(리스트_생성_요청_데이터.items());
        }
    }

    @Nested
    class 리스트_상세_조회 {

        @Test
        void 비회원이_리스트를_상세_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of(정수.getId())));

            // when
            var 결과 = 비회원_리스트_상세_조회_API_호출(동호_리스트.getId()).as(ListDetailResponse.class);

            // then
            assertAll(
                    () -> assertThat(결과.ownerId()).isEqualTo(동호.getId()),
                    () -> assertThat(결과.title()).isEqualTo(동호_리스트.getTitle().getValue()),
                    () -> assertThat(결과.category()).isEqualTo(동호_리스트.getCategory().getViewName()),
                    () -> assertThat(결과.collectCount()).isZero(),
                    () -> assertThat(결과.collaborators()).isEmpty()
            );
        }

        @Test
        void 회원이_리스트를_상세_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            Long 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();
            콜렉트_요청_API_호출(액세스_토큰을_발급한다(정수), 동호_리스트_ID);

            // when
            var 결과 = 회원용_리스트_상세_조회_API_호출(정수_액세스_토큰, 동호_리스트_ID);

            // then
            assertAll(
                    () -> assertThat(결과.ownerId()).isEqualTo(동호.getId()),
                    () -> assertThat(결과.collaborators().get(0).id()).isEqualTo(정수.getId()),
                    () -> assertThat(결과.collectCount()).isEqualTo(1)
            );
        }

        @Test
        void 로그인을_한_상태로_본인이_작성한_리스트를_상세_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            Long 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();
            콜렉트_요청_API_호출(정수_액세스_토큰, 동호_리스트_ID);

            // when
            var 결과 = 회원용_리스트_상세_조회_API_호출(동호_액세스_토큰, 동호_리스트_ID);

            // then
            assertAll(
                    () -> assertThat(결과.ownerId()).isEqualTo(동호.getId()),
                    () -> assertThat(결과.collaborators().get(0).id()).isEqualTo(정수.getId()),
                    () -> assertThat(결과.collectCount()).isEqualTo(1)
            );
        }

        @Test
        void 순위가_변경된_리스트를_상세_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 리스트_수정_요청_데이터 = 아이템_순위와_라벨을_바꾼_좋아하는_견종_TOP3_요청_데이터(List.of());
            리스트_수정_API_호출(리스트_수정_요청_데이터, 동호_액세스_토큰, 동호_리스트_ID);

            // when
            var 결과 = 비회원_리스트_상세_조회_API_호출(동호_리스트_ID).as(ListDetailResponse.class);

            // then
            var 기대값 = ListDetailResponse.of(가장_좋아하는_견종_TOP3_순위_변경(동호, List.of()), 동호, false, List.of());
            리스트_상세_조회를_검증한다(결과, 기대값);
        }

        @Test
        void 탈퇴한_회원의_리스트는_볼_수_없다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();
            회원_탈퇴(new KakaoLogoutResponse(1L), 동호_액세스_토큰);

            // when
            var 응답 = 비회원_리스트_상세_조회_API_호출(동호_리스트_ID);

            // then
            HTTP_상태_코드를_검증한다(응답, BAD_REQUEST);
        }
    }

    @Nested
    class 리스트_수정 {

        @Test
        void 리스트를_성공적으로_수정한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 유진 = 회원을_저장한다(유진());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            // when
            var 리스트_수정_요청_데이터 = 아이템_순위와_라벨을_바꾼_좋아하는_견종_TOP3_요청_데이터(List.of(유진.getId()));
            리스트_수정_API_호출(리스트_수정_요청_데이터, 동호_액세스_토큰, 동호_리스트_ID);

            // then
            var 리스트_상세_조회_결과 = 회원용_리스트_상세_조회_API_호출(동호_액세스_토큰, 동호_리스트_ID);
            ListEntity 수정된_리스트 = 가장_좋아하는_견종_TOP3_순위_변경(동호, List.of());
            ListDetailResponse 기대값 = ListDetailResponse.of(수정된_리스트, 동호, false, List.of(Collaborator.init(유진, 수정된_리스트)));
            리스트_상세_조회를_검증한다(리스트_상세_조회_결과, 기대값);
        }

        @Test
        void 아이템_순위에_변동이_있으면_히스토리로_기록된다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            var 리스트_수정_요청_데이터 = 아이템_순위와_라벨을_바꾼_좋아하는_견종_TOP3_요청_데이터(List.of());
            리스트_수정_API_호출(리스트_수정_요청_데이터, 동호_액세스_토큰, 동호_리스트_ID);

            // when
            var 히스토리_조회_결과 = 비회원_히스토리_조회_API_호출(동호_리스트_ID);
            var 수정된_리스트_상세_조회_결과 = 비회원_리스트_상세_조회_API_호출(동호_리스트_ID).as(ListDetailResponse.class);

            // then
            var 수정된_리스트의_아이템_정보들 = 수정된_리스트_상세_조회_결과.items();
            var 히스토리의_아이템_정보들 = 히스토리_조회_결과.get(0).items();
            assertAll(
                    () -> assertThat(히스토리_조회_결과.size()).isEqualTo(2),
                    () -> assertThat(히스토리_조회_결과.get(히스토리_조회_결과.size() - 1).createdDate()).isEqualTo(수정된_리스트_상세_조회_결과.lastUpdatedDate()),
                    () -> 리스트의_아이템_순위와_히스토리의_아이템_순위를_검증한다(수정된_리스트의_아이템_정보들, 히스토리의_아이템_정보들)
            );
        }

        @Test
        void 리스트의_작성자와_콜라보레이터만_수정할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 유진 = 회원을_저장한다(유진());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 유진_액세스_토큰 = 액세스_토큰을_발급한다(유진);

            var 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰)
                    .as(ListCreateResponse.class);

            // when
            var 수정_요청_데이터 = 아이템_순위와_라벨을_바꾼_좋아하는_견종_TOP3_요청_데이터(List.of(정수.getId()));
            var 동호가_보낸_리스트_수정_API = 리스트_수정_API_호출(수정_요청_데이터, 동호_액세스_토큰, 좋아하는_견종_TOP3_생성_결과.listId());
            var 정수가_보낸_리스트_수정_API = 리스트_수정_API_호출(수정_요청_데이터, 정수_액세스_토큰, 좋아하는_견종_TOP3_생성_결과.listId());
            var 유진이_보낸_리스트_수정_API = 리스트_수정_API_호출(수정_요청_데이터, 유진_액세스_토큰, 좋아하는_견종_TOP3_생성_결과.listId());

            // then
            HTTP_상태_코드를_검증한다(동호가_보낸_리스트_수정_API, NO_CONTENT);
            HTTP_상태_코드를_검증한다(정수가_보낸_리스트_수정_API, NO_CONTENT);
            HTTP_상태_코드를_검증한다(유진이_보낸_리스트_수정_API, FORBIDDEN);
        }
    }

    @Nested
    class 리스트_삭제 {

        @Test
        void 인증_정보가_없으면_리스트_삭제에_실패한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            // when
            var 결과 = 리스트_삭제_요청_API_호출(null, 동호_리스트_ID);

            // then
            HTTP_상태_코드를_검증한다(결과, UNAUTHORIZED);
        }

        @Test
        void 타인의_리스트를_삭제하는_경우_실패한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            // when
            var 결과 = 리스트_삭제_요청_API_호출(정수_액세스_토큰, 동호_리스트_ID);

            // then
            HTTP_상태_코드를_검증한다(결과, FORBIDDEN);
        }

        @Test
        void 리스트를_삭제한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 동호_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            // when
            var 삭제_결과 = 리스트_삭제_요청_API_호출(동호_액세스_토큰, 동호_리스트_ID);
            var 조회_결과 = 비회원_리스트_상세_조회_API_호출(동호_리스트_ID);

            // then
            HTTP_상태_코드를_검증한다(삭제_결과, NO_CONTENT);
            HTTP_상태_코드를_검증한다(조회_결과, NOT_FOUND);
        }
    }

    @Nested
    class 피드_리스트_조회 {

        @Test
        void 비회원이_피드_리스트를_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_리스트_1 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));
            var 동호_리스트_2 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));

            // when
            var 결과 = 비회원_피드_리스트_조회(동호).as(FindFeedListResponse.class);
            var 기대값 = List.of(FeedListInfo.of(동호_리스트_2), FeedListInfo.of(동호_리스트_1));

            // then
            assertThat(결과.hasNext()).isFalse();
            assertThat(결과.feedLists()).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(기대값);
        }

        @Test
        void 회원이_피드_리스트를_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            리스트를_저장한다(가장_좋아하는_견종_TOP3(동호, List.of()));
            var 동호_리스트_2 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_3 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_4 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_5 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_6 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_7 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_8 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_9 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_10 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 동호_리스트_11 = 리스트를_저장한다(좋아하는_라면_TOP3(동호, List.of()));
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);

            // when
            var allUserListsResponse = 회원_피드_리스트_조회(동호, 정수_액세스_토큰).as(FindFeedListResponse.class);

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
            var 동호 = 회원을_저장한다(동호());
            var 동호_리스트들 = 지정된_개수만큼_리스트를_생성한다(동호, 11);
            리스트를_모두_저장한다(동호_리스트들);

            // when
            var 결과 = 비회원이_피드_리스트_조회_카테고리_필터링_요청(동호, "book").as(FindFeedListResponse.class);

            var 필터링_조건 = CategoryType.nameOf("book");
            var 기대값 = 동호_리스트들.stream()
                    .sorted(comparing(ListEntity::getId, reverseOrder()))
                    .filter(list -> list.isCategoryType(필터링_조건))
                    .map(FeedListInfo::of)
                    .toList();

            // then
            assertThat(결과.feedLists()).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(기대값);
        }

        @Test
        void 콜라보레이터로_필터링한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 리스트_1_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();
            var 리스트_2_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of(동호.getId())), 정수_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();

            // when
            var 결과 = 비회원이_피드_리스트_조회_콜라보레이터_필터링_요청(동호).as(FindFeedListResponse.class);

            // then
            assertThat(결과.feedLists()).hasSize(2);
            assertThat(결과.feedLists().get(0).id()).isEqualTo(리스트_2_ID);
            assertThat(결과.feedLists().get(1).id()).isEqualTo(리스트_1_ID);
        }

        @Test
        void 콜라보레이터와_카테고리로_필터링한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            var 동호_리스트_2 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of(정수.getId())), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            var 결과 = 비회원이_피드_리스트_조회_카테고리_콜라보레이터_필터링_요청(동호, "etc").as(FindFeedListResponse.class);

            // then
            assertThat(결과.feedLists()).hasSize(1);
            assertThat(결과.feedLists().get(0).id()).isEqualTo(동호_리스트_2.listId());
        }
    }

    @Nested
    class 리스트_탐색 {

        @Test
        void 트랜딩_리스트를_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(정수, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));

            // when
            List<ListTrandingResponse> 결과 = 트랜딩_리스트_조회_API_호출().as(new TypeRef<>() {
            });

            // then
            var 동호_리스트 = 비회원_피드_리스트_조회(동호).as(FindFeedListResponse.class).feedLists();
            var 정수_리스트 = 비회원_피드_리스트_조회(정수).as(FindFeedListResponse.class).feedLists();
            var 모든_리스트 = new ArrayList<>(동호_리스트);
            모든_리스트.addAll(정수_리스트);

            var 대표_이미지들 = 모든_리스트.stream()
                    .sorted(comparing(FeedListInfo::id, reverseOrder()))
                    .map(feedListInfo -> feedListInfo.listItems().stream()
                            .sorted(comparing(ListItemsResponse::rank))
                            .filter(listItemsResponse -> listItemsResponse.imageUrl() != null && !listItemsResponse.imageUrl().isBlank())
                            .map(ListItemsResponse::imageUrl)
                            .findFirst()
                            .orElse(""))
                    .toList();

            assertThat(결과.stream().map(ListTrandingResponse::id)).isEqualTo(모든_리스트.stream()
                    .sorted(comparing(FeedListInfo::id, reverseOrder()))
                    .map(FeedListInfo::id)
                    .limit(10)
                    .toList());
            assertThat(결과).usingRecursiveComparison()
                    .comparingOnlyFields("itemImageUrl")
                    .isEqualTo(대표_이미지들);
        }

        @Test
        void 회원이_최신_리스트_10개를_조회하면_팔로우한_사용자의_최신_리스트_10개가_반환된다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            팔로우_요청_API(동호_액세스_토큰, 정수.getId());
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(정수, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));

            // when
            var response = 회원_최신_리스트_10개_조회_API_호출(동호_액세스_토큰);
            var result = response.as(ListRecentResponse.class);

            // then
            assertThat(result.lists()).hasSize(5);
        }

        @Test
        void 비회원이_최신_리스트_10개를_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            팔로우_요청_API(동호_액세스_토큰, 정수.getId());
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(정수, 5));
            리스트를_모두_저장한다(지정된_개수만큼_리스트를_생성한다(동호, 5));

            // when
            var 결과 = 비회원_최신_리스트_10개_조회_API_호출().as(ListRecentResponse.class);

            // then
            var 동호_리스트 = 비회원_피드_리스트_조회(동호).as(FindFeedListResponse.class).feedLists();
            var 정수_리스트 = 비회원_피드_리스트_조회(정수).as(FindFeedListResponse.class).feedLists();
            var 모든_리스트 = new ArrayList<>(동호_리스트);
            모든_리스트.addAll(정수_리스트);

            var 기댓값 = 모든_리스트.stream()
                    .sorted(comparing(FeedListInfo::id, reverseOrder()))
                    .filter(FeedListInfo::isPublic)
                    .map(FeedListInfo::id)
                    .limit(10)
                    .toList();

            assertAll(
                    () -> assertThat(결과.lists()).hasSize(10),
                    () -> assertThat(결과.lists()).usingRecursiveComparison()
                            .comparingOnlyFields("id")
                            .isEqualTo(기댓값),
                    () -> assertThat(결과.lists()).allMatch(listResponse -> listResponse.items().size() == 3)
            );
        }
    }

    @Nested
    class 리스트_검색 {

        @Test
        void 키워드로_검색할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰);
            var 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            var 결과 = 키워드로_검색_API_호출("라면").as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(결과.totalCount()).isOne(),
                    () -> assertThat(결과.resultLists()).hasSize(1),
                    () -> assertThat(결과.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId())
            );
        }

        @Test
        void 카테고리로_필터링_할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰);
            var 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            var result = 카테고리로_검색_API_호출("etc").as(ListSearchResponse.class);

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
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            var 결과 = 카테고리와_키워드로_검색_API_호출("animal_plant", "견종").as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(결과.totalCount()).isOne(),
                    () -> assertThat(결과.resultLists()).hasSize(1),
                    () -> assertThat(결과.resultLists().get(0).id()).isEqualTo(좋아하는_견종_TOP3_생성_결과.listId())
            );
        }

        @Test
        void 최신순으로_정렬할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            var 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            var 결과 = 검색_API_호출().as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(결과.totalCount()).isEqualTo(2),
                    () -> assertThat(결과.resultLists()).hasSize(2),
                    () -> assertThat(결과.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId()),
                    () -> assertThat(결과.resultLists().get(1).id()).isEqualTo(좋아하는_견종_TOP3_생성_결과.listId())
            );
        }

        @Test
        void 오래된순으로_정렬할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            var 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            var result = 정렬기준을_포함한_검색_API_호출("old").as(ListSearchResponse.class);

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
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 좋아하는_견종_TOP3_생성_결과 = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            var 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            콜렉트_요청_API_호출(정수_액세스_토큰, 좋아하는_라면_TOP3_생성_결과.listId());

            // when
            var 결과 = 정렬기준을_포함한_검색_API_호출("collect").as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(결과.totalCount()).isEqualTo(2),
                    () -> assertThat(결과.resultLists()).hasSize(2),
                    () -> assertThat(결과.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId()),
                    () -> assertThat(결과.resultLists().get(1).id()).isEqualTo(좋아하는_견종_TOP3_생성_결과.listId())
            );
        }

        // TODO: 테스트셋 보충 필요
        @Test
        void 관련도순으로_정렬할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);
            var 좋아하는_라면_TOP3_생성_결과 = 리스트_저장_API_호출(좋아하는_라면_TOP3_생성_요청_데이터(List.of()), 동호_액세스_토큰).as(ListCreateResponse.class);

            // when
            var 결과 = 키워드와_정렬기준을_포함한_검색_API_호출("라면", "related").as(ListSearchResponse.class);

            // then
            assertAll(
                    () -> assertThat(결과.totalCount()).isOne(),
                    () -> assertThat(결과.resultLists()).hasSize(1),
                    () -> assertThat(결과.resultLists().get(0).id()).isEqualTo(좋아하는_라면_TOP3_생성_결과.listId())
            );
        }
    }
}
