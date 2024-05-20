package com.listywave.acceptance.follow;

import static com.listywave.acceptance.common.CommonAcceptanceHelper.HTTP_상태_코드를_검증한다;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로우_요청_API;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로우_취소_API;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로워_검색_API;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로워_목록_조회_API;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로워_삭제_API;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로잉_검색_API;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로잉_목록_조회_API;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.비회원_회원_정보_조회_요청;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.유진;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.user.application.dto.FollowersResponse;
import com.listywave.user.application.dto.FollowersResponse.FollowerInfo;
import com.listywave.user.application.dto.FollowingsResponse;
import com.listywave.user.application.dto.FollowingsResponse.FollowingInfo;
import com.listywave.user.application.dto.UserInfoResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("팔로우 관련 인수테스트")
public class FollowAcceptanceTest extends AcceptanceTest {

    @Nested
    class 팔로우_요청 {

        @Test
        void 성공적으로_팔로우_한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            var 응답 = 팔로우_요청_API(동호_액세스_토큰, 정수.getId());

            // then
            HTTP_상태_코드를_검증한다(응답, NO_CONTENT);

            // TODO: User 객체의 followerCount, followingCount가 오르는 걸 검증해야 함
//            assertAll(
//                    () -> HTTP_상태_코드를_검증한다(응답, NO_CONTENT),
//                    () -> assertThat(동호.getFollowingCount()).isEqualTo(동호().getFollowingCount() + 1),
//                    () -> assertThat(정수.getFollowerCount()).isEqualTo(정수().getFollowerCount() + 1)
//            );
        }

        @Test
        void 본인을_팔로우_할_수_없다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            var 응답 = 팔로우_요청_API(동호_액세스_토큰, 동호.getId());

            // then
            HTTP_상태_코드를_검증한다(응답, FORBIDDEN);
        }

        @Test
        void 이미_팔로우가_되어_있다면_예외가_발생한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            팔로우_요청_API(동호_액세스_토큰, 정수.getId());

            // when
            var 응답 = 팔로우_요청_API(동호_액세스_토큰, 정수.getId());

            // then
            HTTP_상태_코드를_검증한다(응답, BAD_REQUEST);
        }

        @Test
        void 팔로우가_성공하면_팔로워의_followingCount와_팔로잉의_follwerCount가_1씩_증가한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            팔로우_요청_API(동호_액세스_토큰, 정수.getId());

            // when
            var 동호_정보 = 비회원_회원_정보_조회_요청(동호.getId()).as(UserInfoResponse.class);
            var 정수_정보 = 비회원_회원_정보_조회_요청(정수.getId()).as(UserInfoResponse.class);

            // then
            assertAll(
                    () -> assertThat(동호_정보.followerCount()).isEqualTo(동호.getFollowerCount()),
                    () -> assertThat(동호_정보.followingCount()).isEqualTo(동호.getFollowingCount() + 1),
                    () -> assertThat(정수_정보.followerCount()).isEqualTo(정수.getFollowerCount() + 1),
                    () -> assertThat(정수_정보.followingCount()).isEqualTo(정수.getFollowingCount())
            );
        }
    }

    @Nested
    class 팔로우_취소 {

        @Test
        void 팔로우를_성공적으로_취소한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            팔로우_요청_API(동호_액세스_토큰, 정수.getId());

            // when
            var 응답 = 팔로우_취소_API(동호_액세스_토큰, 정수.getId());

            // then
            HTTP_상태_코드를_검증한다(응답, NO_CONTENT);
        }

        @Test
        void 이미_팔로우_상태가_아니라면_예외가_발생한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);

            // when
            var 응답 = 팔로우_취소_API(동호_액세스_토큰, 정수.getId());

            // then
            HTTP_상태_코드를_검증한다(응답, BAD_REQUEST);
        }

        @Test
        void 팔로우가_취소되면_팔로워의_followingCount와_팔로잉의_followerCount는_1씩_감소한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            팔로우_요청_API(동호_액세스_토큰, 정수.getId());

            // when
            팔로우_취소_API(동호_액세스_토큰, 정수.getId());

            // then
            var 동호_정보 = 비회원_회원_정보_조회_요청(동호.getId()).as(UserInfoResponse.class);
            var 정수_정보 = 비회원_회원_정보_조회_요청(정수.getId()).as(UserInfoResponse.class);

            assertAll(
                    () -> assertThat(동호_정보.followerCount()).isEqualTo(동호.getFollowerCount()),
                    () -> assertThat(동호_정보.followingCount()).isEqualTo(동호.getFollowingCount()),
                    () -> assertThat(정수_정보.followerCount()).isEqualTo(정수.getFollowerCount()),
                    () -> assertThat(정수_정보.followingCount()).isEqualTo(정수.getFollowingCount())
            );
        }
    }

    @Nested
    class 팔로워_삭제 {

        @Test
        void 팔로워를_삭제할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            팔로우_요청_API(동호_액세스_토큰, 정수.getId());

            // when
            팔로워_삭제_API(정수_액세스_토큰, 동호.getId());

            // then
            var 정수_팔로워_목록 = 팔로워_목록_조회_API(정수.getId()).as(FollowersResponse.class);
            assertThat(정수_팔로워_목록.followers()).isEmpty();
        }
    }

    @Nested
    class 팔로워_목록_조회 {

        @Test
        void 성공적으로_팔로워_목록을_조회한다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 유진 = 회원을_저장한다(유진());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            팔로우_요청_API(동호_액세스_토큰, 유진.getId());
            팔로우_요청_API(정수_액세스_토큰, 유진.getId());

            // when
            var 결과 = 팔로워_목록_조회_API(유진.getId()).as(FollowersResponse.class);

            // then
            var 기댓값 = List.of(FollowerInfo.of(동호), FollowerInfo.of(정수));
            assertThat(결과.followers()).isEqualTo(기댓값);
        }

        @Test
        void 검색어로_팔로워하고_있는_회원을_조회할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 유진 = 회원을_저장한다(유진());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
            팔로우_요청_API(동호_액세스_토큰, 유진.getId());
            팔로우_요청_API(정수_액세스_토큰, 유진.getId());

            // when
            var 결과 = 팔로워_검색_API(유진.getId(), 동호.getNickname()).as(FollowersResponse.class);

            // then
            var 기댓값 = List.of(FollowerInfo.of(동호));
            assertThat(결과.followers()).isEqualTo(기댓값);
        }
    }

    @Nested
    class 팔로잉_목록_조회 {

        @Test
        void 성공적으로_팔로잉_목록을_조회한다_닉네임으로_정렬이_되어있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 유진 = 회원을_저장한다(유진());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            팔로우_요청_API(동호_액세스_토큰, 정수.getId());
            팔로우_요청_API(동호_액세스_토큰, 유진.getId());

            // when
            var 결과 = 팔로잉_목록_조회_API(동호.getId()).as(FollowingsResponse.class);

            // then
            var 기댓값 = List.of(FollowingInfo.of(유진), FollowingInfo.of(정수));
            assertThat(결과.followings()).isEqualTo(기댓값);
        }

        @Test
        void 검색어로_팔로잉하고_있는_회원을_조회할_수_있다() {
            // given
            var 동호 = 회원을_저장한다(동호());
            var 정수 = 회원을_저장한다(정수());
            var 유진 = 회원을_저장한다(유진());
            var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
            팔로우_요청_API(동호_액세스_토큰, 정수.getId());
            팔로우_요청_API(동호_액세스_토큰, 유진.getId());

            // when
            var 결과 = 팔로잉_검색_API(동호.getId(), 유진.getNickname()).as(FollowingsResponse.class);

            // then
            var 기댓값 = List.of(FollowingInfo.of(유진));
            assertThat(결과.followings()).isEqualTo(기댓값);
        }
    }
}
