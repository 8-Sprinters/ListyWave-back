package com.listywave.acceptance.user;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.HTTP_상태_코드를_검증한다;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로우_요청_API;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.*;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.*;
import static com.listywave.user.fixture.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.*;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.dto.UsersRecommendedResponse;
import com.listywave.user.application.dto.search.UserSearchResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("회원 관련 인수테스트")
public class UserAcceptanceTest extends AcceptanceTest {

    @Nested
    class 회원_상세_조회 {

        @Test
        void 비회원이_회원_상세_정보를_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());

            // when
            var 결과 = 비회원_회원_정보_조회_요청(동호.getId()).as(UserInfoResponse.class);

            // then
            var 기댓값 = UserInfoResponse.of(동호, false, false);
            assertThat(결과).usingRecursiveComparison()
                    .isEqualTo(기댓값);
        }

        @Test
        void 회원이_다른_회원_상세_정보를_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);

            // when
            var 결과 = 회원이_회원_정보_조회_요청(정수_액세스_토큰, 동호.getId()).as(UserInfoResponse.class);

            // then
            var 기댓값 = UserInfoResponse.of(동호, false, false);
            assertThat(결과).usingRecursiveComparison()
                    .isEqualTo(기댓값);
        }

        @Test
        void 회원이_본인의_상세_정보를_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            var 결과 = 회원이_회원_정보_조회_요청(동호_액세스_토큰, 동호.getId()).as(UserInfoResponse.class);

            // then
            var 기댓값 = UserInfoResponse.of(동호, false, true);
            assertThat(결과).usingRecursiveComparison()
                    .isEqualTo(기댓값);
        }
    }

    @Nested
    class 회원_검색 {

        @Test
        void 비회원이_회원을_검색한다_이때_검색어가_없으면_결과가_존재하지_않는다() {
            // given
            회원을_저장한다(동호());
            회원을_저장한다(정수());
            회원을_저장한다(유진());

            // when
            var 결과 = 비회원이_사용자_검색().as(UserSearchResponse.class);

            // then
            assertThat(결과.totalCount()).isZero();
        }

        @Test
        void 비회원이_회원을_검색한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            회원을_저장한다(정수());
            회원을_저장한다(유진());

            // when
            var 결과 = 비회원이_사용자_검색(동호.getNickname()).as(UserSearchResponse.class);

            // then
            assertThat(결과.totalCount()).isOne();
            assertThat(결과.users().get(0).getNickname()).isEqualTo(동호.getNickname());
        }

        @Test
        void 회원이_전체_검색을_하면_본인을_제외하고_검색된다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            회원을_저장한다(정수());
            회원을_저장한다(유진());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            var 결과 = 회원이_사용자_검색(동호_액세스_토큰, 동호.getNickname()).as(UserSearchResponse.class);

            // then
            assertThat(결과.totalCount()).isZero();
        }
    }

    @Test
    void 닉네임_중복을_체크한다() {
        assertAll(
                () -> assertThat(닉네임_중복_체크_요청(동호().getNickname()).as(Boolean.class)).isFalse(),

                () -> {
                    회원을_저장한다(동호());
                    assertThat(닉네임_중복_체크_요청(동호().getNickname()).as(Boolean.class)).isTrue();
                },

                // 대소문자 구분없이 중복 체크
                () -> assertThat(닉네임_중복_체크_요청("kdkdhoho").as(Boolean.class)).isTrue(),
                () -> assertThat(닉네임_중복_체크_요청("KDKDHOHO").as(Boolean.class)).isTrue(),
                () -> assertThat(닉네임_중복_체크_요청("KdkdHoho").as(Boolean.class)).isTrue()
        );
        ;
    }

    @Nested
    class 추천_사용자 {

        @Test
        void 회원이_추천_사용자_조회한다_추천_사용자는_팔로잉하지_않는_유저여야한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 유진 = 회원을_저장한다(유진());
            팔로우_요청_API(액세스_토큰을_발급한다(동호), 정수.getId());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            List<UsersRecommendedResponse> 결과 = 회원_추천_API(동호_액세스_토큰).as(new TypeRef<>() {
            });

            // then
            assertThat(결과).hasSize(1);
            assertThat(결과.get(0).nickname()).isEqualTo(유진.getNickname());
        }

        @Test
        void 비회원이_추천_사용자_조회한다() {
            // given
            회원을_저장한다(동호());
            회원을_저장한다(정수());
            회원을_저장한다(유진());

            // when
            List<UsersRecommendedResponse> 결과 = 비회원_추천_API().as(new TypeRef<>() {
            });

            // then
            assertThat(결과).hasSize(3);
        }

        @Test
        void 가장_최근에_리스트_생성_또는_수정한_사용자_순위대로_추천_사용자_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 유진 = 회원을_저장한다(유진());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 유진_액세스_토큰 = 액세스_토큰을_발급한다(유진);

            var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());
            var 동호_리스트_ID = 리스트_저장_API_호출(리스트_생성_요청_데이터, 동호_액세스_토큰)
                    .as(ListCreateResponse.class)
                    .listId();
            리스트_저장_API_호출(리스트_생성_요청_데이터, 정수_액세스_토큰);
            리스트_저장_API_호출(리스트_생성_요청_데이터, 유진_액세스_토큰);

            var 리스트_수정_요청_데이터 = 아이템_순위와_라벨을_바꾼_좋아하는_견종_TOP3_요청_데이터(List.of(유진.getId()));
            리스트_수정_API_호출(리스트_수정_요청_데이터, 동호_액세스_토큰, 동호_리스트_ID);

            // when
            List<UsersRecommendedResponse> 결과 = 비회원_추천_API().as(new TypeRef<>() {
            });

            // then
            assertAll(
                    () -> assertThat(결과).hasSize(3),
                    () -> assertThat(결과.get(0).nickname()).isEqualTo(동호.getNickname()),
                    () -> assertThat(결과.get(1).nickname()).isEqualTo(유진.getNickname()),
                    () -> assertThat(결과.get(2).nickname()).isEqualTo(정수.getNickname())
            );
        }
    }

    @Nested
    class 프로필_수정 {

        @Test
        void 이미지_변경없이_프로필_수정_가능하다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            var 프로필_수정_요청_데이터 = 프로필_수정_요청_데이터(
                    "사랑이형",
                    "사랑이형입니다",
                    동호.getProfileImageUrl(),
                    동호.getBackgroundImageUrl()
            );

            // when
            var 응답 = 프로필_수정_요청(동호_액세스_토큰, 동호.getId(), 프로필_수정_요청_데이터);
            var 수정_이후_사용자_조회_결과 = 비회원_회원_정보_조회_요청(동호.getId()).as(UserInfoResponse.class);

            // then
            HTTP_상태_코드를_검증한다(응답, NO_CONTENT);
            assertThat(수정_이후_사용자_조회_결과.nickname()).isEqualTo("사랑이형");
            assertThat(수정_이후_사용자_조회_결과.description()).isEqualTo("사랑이형입니다");
        }

        @Test
        void 닉네임만_수정이_가능하다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 프로필_수정_요청_데이터 = 프로필_수정_요청_데이터("사랑이형", null, null, null);

            // when
            var 응답 = 프로필_수정_요청(동호_액세스_토큰, 동호.getId(), 프로필_수정_요청_데이터);

            // then
            HTTP_상태_코드를_검증한다(응답, NO_CONTENT);

            var 수정_이후_사용자_조회_결과 = 비회원_회원_정보_조회_요청(동호.getId()).as(UserInfoResponse.class);
            assertThat(수정_이후_사용자_조회_결과.nickname()).isEqualTo("사랑이형");
            assertThat(수정_이후_사용자_조회_결과.description()).isEqualTo(동호.getDescription());
            assertThat(수정_이후_사용자_조회_결과.profileImageUrl()).isEqualTo(동호.getProfileImageUrl());
            assertThat(수정_이후_사용자_조회_결과.backgroundImageUrl()).isEqualTo(동호.getBackgroundImageUrl());
        }

        @Test
        void 수정한_닉네임이_이미_존재하는_닉네임인_경우_400_에러가_발생한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            var 닉네임_변경_요청 = 프로필_수정_요청_데이터(정수.getNickname(), null, null, null);
            var 응답 = 프로필_수정_요청(동호_액세스_토큰, 동호.getId(), 닉네임_변경_요청);

            // then
            HTTP_상태_코드를_검증한다(응답, BAD_REQUEST);
        }

        @Test
        void 타인의_프로필을_수정하면_403_에러가_발생한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            var 프로필_수정_요청_데이터 = 프로필_수정_요청_데이터("사랑이형", null, null, null);

            // when
            var 응답 = 프로필_수정_요청(정수_액세스_토큰, 동호.getId(), 프로필_수정_요청_데이터);

            // then
            HTTP_상태_코드를_검증한다(응답, FORBIDDEN);
        }
    }
}
