package com.listywave.acceptance.user;

import static com.listywave.acceptance.user.UserAcceptanceTestHelper.닉네임_중복_체크_요청;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.비회원_회원_정보_조회_요청;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.비회원이_사용자_검색;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.추천_사용자_조회;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.프로필_수정_요청;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.프로필_수정_요청_데이터;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.회원이_사용자_검색;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.회원이_회원_정보_조회_요청;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.유진;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.user.application.domain.User;
import com.listywave.user.application.dto.RecommendUsersResponse;
import com.listywave.user.application.dto.UserInfoResponse;
import com.listywave.user.application.dto.search.UserSearchResponse;
import com.listywave.user.presentation.dto.UserProfileUpdateRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
            User 동호 = 회원을_저장한다(동호());

            // when
            UserInfoResponse result = 비회원_회원_정보_조회_요청(동호.getId()).as(UserInfoResponse.class);

            // then
            UserInfoResponse expect = UserInfoResponse.of(동호, false, false);
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expect);
        }

        @Test
        void 회원이_다른_회원_상세_정보를_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);

            // when
            UserInfoResponse result = 회원이_회원_정보_조회_요청(정수_액세스_토큰, 동호.getId()).as(UserInfoResponse.class);

            // then
            UserInfoResponse expect = UserInfoResponse.of(동호, false, false);
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expect);
        }

        @Test
        void 회원이_팔로우_하고_있는_회원의_상세_정보를_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            팔로우를_저장한다(정수, 동호);
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);

            // when
            UserInfoResponse result = 회원이_회원_정보_조회_요청(정수_액세스_토큰, 동호.getId()).as(UserInfoResponse.class);

            // then
            UserInfoResponse expect = UserInfoResponse.of(동호, true, false);
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expect);
        }

        @Test
        void 회원이_본인의_상세_정보를_조회한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            UserInfoResponse result = 회원이_회원_정보_조회_요청(동호_액세스_토큰, 동호.getId()).as(UserInfoResponse.class);

            // then
            UserInfoResponse expect = UserInfoResponse.of(동호, false, true);
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expect);
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
            UserSearchResponse result = 비회원이_사용자_검색().as(UserSearchResponse.class);

            // then
            assertThat(result.totalCount()).isZero();
        }

        @Test
        void 비회원이_회원을_검색한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            회원을_저장한다(정수());
            회원을_저장한다(유진());

            // when
            UserSearchResponse result = 비회원이_사용자_검색(동호.getNickname()).as(UserSearchResponse.class);

            // then
            assertThat(result.totalCount()).isOne();
            assertThat(result.users().get(0).getNickname()).isEqualTo(동호.getNickname());
        }

        @Test
        void 회원이_전체_검색을_하면_본인을_제외하고_검색된다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            회원을_저장한다(정수());
            회원을_저장한다(유진());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            UserSearchResponse result = 회원이_사용자_검색(동호_액세스_토큰, 동호.getNickname()).as(UserSearchResponse.class);

            // then
            assertThat(result.totalCount()).isZero();
        }
    }

    @Test
    void 닉네임_중복을_체크한다() {
        Boolean result1 = 닉네임_중복_체크_요청(동호().getNickname()).as(Boolean.class);
        assertThat(result1).isFalse();

        회원을_저장한다(동호());
        Boolean result2 = 닉네임_중복_체크_요청(동호().getNickname()).as(Boolean.class);
        assertThat(result2).isTrue();
    }

    @Test
    void 회원이_추천_사용자_조회한다_추천_사용자는_팔로잉하지_않는_유저여야한다() {
        // given
        User 동호 = 회원을_저장한다(동호());
        User 정수 = 회원을_저장한다(정수());
        User 유진 = 회원을_저장한다(유진());
        팔로우를_저장한다(동호, 정수);
        String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

        // when
        List<RecommendUsersResponse> result = 추천_사용자_조회(동호_액세스_토큰).as(new TypeRef<>() {
        });

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).nickname()).isEqualTo(유진.getNickname());
    }

    @Nested
    class 프로필_수정 {

        @Test
        void 이미지_변경없이_프로필을_수정한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            UserProfileUpdateRequest 프로필_수정_요청_데이터 = 프로필_수정_요청_데이터(
                    "사랑이형",
                    "사랑이형입니다",
                    동호.getProfileImageUrl(),
                    동호.getBackgroundImageUrl()
            );

            // when
            ExtractableResponse<Response> response = 프로필_수정_요청(동호_액세스_토큰, 동호.getId(), 프로필_수정_요청_데이터);

            // then
            assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());

            UserInfoResponse 수정_이후_사용자_조회_결과 = 비회원_회원_정보_조회_요청(동호.getId()).as(UserInfoResponse.class);
            assertThat(수정_이후_사용자_조회_결과.nickname()).isEqualTo("사랑이형");
            assertThat(수정_이후_사용자_조회_결과.description()).isEqualTo("사랑이형입니다");
        }

        @Test
        void 닉네임만_수정할_수_있다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            UserProfileUpdateRequest 프로필_수정_요청_데이터 = 프로필_수정_요청_데이터(
                    "사랑이형",
                    null,
                    null,
                    null
            );

            // when
            ExtractableResponse<Response> response = 프로필_수정_요청(동호_액세스_토큰, 동호.getId(), 프로필_수정_요청_데이터);

            // then
            assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());

            UserInfoResponse 수정_이후_사용자_조회_결과 = 비회원_회원_정보_조회_요청(동호.getId()).as(UserInfoResponse.class);
            assertThat(수정_이후_사용자_조회_결과.nickname()).isEqualTo("사랑이형");
            assertThat(수정_이후_사용자_조회_결과.description()).isEqualTo(동호.getDescription());
            assertThat(수정_이후_사용자_조회_결과.profileImageUrl()).isEqualTo(동호.getProfileImageUrl());
            assertThat(수정_이후_사용자_조회_결과.backgroundImageUrl()).isEqualTo(동호.getBackgroundImageUrl());
        }

        @Test
        void 수정한_닉네임이_이미_존재하는_닉네임인_경우_400_에러가_발생한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            UserProfileUpdateRequest 닉네임_변경_요청 = 프로필_수정_요청_데이터(정수.getNickname(), null, null, null);
            ExtractableResponse<Response> response = 프로필_수정_요청(동호_액세스_토큰, 동호.getId(), 닉네임_변경_요청);

            // then
            assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        }

        @Test
        void 타인의_프로필을_수정하면_403_에러가_발생한다() {
            // given
            User 동호 = 회원을_저장한다(동호());
            User 정수 = 회원을_저장한다(정수());
            String 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            UserProfileUpdateRequest 프로필_수정_요청_데이터 = 프로필_수정_요청_데이터(
                    "사랑이형",
                    null,
                    null,
                    null
            );

            // when
            ExtractableResponse<Response> response = 프로필_수정_요청(정수_액세스_토큰, 동호.getId(), 프로필_수정_요청_데이터);

            // then
            assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
        }
    }
}
