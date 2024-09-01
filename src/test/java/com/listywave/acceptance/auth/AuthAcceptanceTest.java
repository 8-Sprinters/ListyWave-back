package com.listywave.acceptance.auth;

import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.로그아웃_API;
import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.로그인_API;
import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.액세스_토큰_재발급_API;
import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.카카오_로그인_페이지_요청_API;
import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.회원탈퇴_API;
import static com.listywave.acceptance.common.CommonAcceptanceHelper.HTTP_상태_코드를_검증한다;
import static com.listywave.acceptance.user.UserAcceptanceTestHelper.비회원_회원_정보_조회_요청;
import static com.listywave.common.exception.ErrorCode.DELETED_USER_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.auth.application.dto.LoginResponse;
import com.listywave.auth.application.dto.UpdateTokenResponse;
import com.listywave.auth.infra.kakao.response.KakaoLogoutResponse;
import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoMember.KakaoAccount;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import com.listywave.common.exception.ErrorResponse;
import com.listywave.image.application.domain.DefaultBackgroundImages;
import com.listywave.image.application.domain.DefaultProfileImages;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("인증 관련 인수테스트")
public class AuthAcceptanceTest extends AcceptanceTest {

    private static final KakaoTokenResponse EXPECTED_KAKAO_TOKEN_RESPONSE = new KakaoTokenResponse("Bearer", "accessToken", Integer.MAX_VALUE, "refreshToken", Integer.MAX_VALUE, "email");
    private static final KakaoMember EXPECTED_KAKAO_MEMBER = new KakaoMember(1L, new KakaoAccount(true, true, true, "email"));

    @Nested
    class 로그인 {

        @Test
        void 카카오_로그인_페이지를_요청한다() {
            var 응답 = 카카오_로그인_페이지_요청_API();

            assertThat(응답.statusCode()).isEqualTo(OK.value());
        }

        @Test
        void 최초_로그인을_성공적으로_수행한다() {
            // given
            var kakaoTokenResponse = new KakaoTokenResponse("Bearer", "AccessToken", Integer.MAX_VALUE, "RefreshToken", Integer.MAX_VALUE, "email");
            when(kakaoOauthApiClient.requestToken(any())).thenReturn(kakaoTokenResponse);

            KakaoMember kakaoMember = new KakaoMember(1L, new KakaoAccount(true, true, true, "listywave@kakao.com"));
            when(kakaoOauthApiClient.fetchKakaoMember(anyString())).thenReturn(kakaoMember);

            // when
            var 로그인_응답 = 로그인_API();
            var 로그인_결과 = 로그인_응답.as(LoginResponse.class);

            // then
            assertAll(
                    () -> HTTP_상태_코드를_검증한다(로그인_응답, OK),
                    () -> assertThat(로그인_결과.isFirst()).isEqualTo(true),
                    () -> assertThat(jwtManager.readTokenWithPrefix("Bearer " + 로그인_결과.accessToken())).isEqualTo(1L),
                    () -> assertThat(jwtManager.readTokenWithoutPrefix(로그인_결과.refreshToken())).isEqualTo(1L),
                    () -> assertThat(로그인_결과.profileImageUrl()).isIn(
                            Arrays.stream(DefaultProfileImages.values())
                                    .map(DefaultProfileImages::getValue)
                                    .toList()
                    ),
                    () -> assertThat(로그인_결과.backgroundImageUrl()).isIn(
                            Arrays.stream(DefaultBackgroundImages.values())
                                    .map(DefaultBackgroundImages::getValue)
                                    .toList()
                    ),
                    () -> assertThat(로그인_결과.followingCount()).isEqualTo(0),
                    () -> assertThat(로그인_결과.followerCount()).isEqualTo(0)
            );
        }

        @Test
        void 로그인_시_이미_DB에_회원이_있으면_isFirst에_false를_반환한다() {
            // given
            로그인(EXPECTED_KAKAO_TOKEN_RESPONSE, EXPECTED_KAKAO_MEMBER);

            // when
            var 로그인_응답 = 로그인(EXPECTED_KAKAO_TOKEN_RESPONSE, EXPECTED_KAKAO_MEMBER);
            var 로그인_결과 = 로그인_응답.as(LoginResponse.class);

            // then
            HTTP_상태_코드를_검증한다(로그인_응답, OK);
            assertThat(로그인_결과.isFirst()).isFalse();
        }

        @Test
        void 로그인에_성공하면_Body와_Authorization헤더에_액세스_토큰과_리프레시_토큰을_담아_응답한다() {
            // when
            var 로그인_응답 = 로그인(EXPECTED_KAKAO_TOKEN_RESPONSE, EXPECTED_KAKAO_MEMBER);
            var 로그인_결과 = 로그인_응답.as(LoginResponse.class);

            // then
            assertAll(
                    () -> assertThat(로그인_결과.accessToken()).isNotNull().isNotBlank(),
                    () -> assertThat(로그인_결과.refreshToken()).isNotNull().isNotBlank()
            );
        }
    }

    @Nested
    class 액세스_토큰_재발급 {

        @Test
        void 리프레시_토큰으로_액세스_토큰을_재발급받는다() throws InterruptedException {
            // given
            var 로그인_결과 = 로그인(EXPECTED_KAKAO_TOKEN_RESPONSE, EXPECTED_KAKAO_MEMBER);
            var 로그인_응답값 = 로그인_결과.as(LoginResponse.class);
            var 액세스_토큰 = 로그인_응답값.accessToken();
            var 리프레시_토큰 = 로그인_응답값.refreshToken();

            // when
            Thread.sleep(1000); // 동일한 시점에 액세스 토큰이 재발급되는 것을 방지하기 위한 딜레이
            var 토큰_재발급_결과 = 액세스_토큰_재발급_API(리프레시_토큰);
            var 재발급된_액세스_토큰 = 토큰_재발급_결과.as(UpdateTokenResponse.class).accessToken();

            // then
            assertThat(재발급된_액세스_토큰)
                    .isNotNull()
                    .isNotBlank()
                    .isNotEqualTo(액세스_토큰);
        }
    }

    @Nested
    class 로그아웃 {

        @Test
        void 로그아웃을_한다() {
            // given
            var 로그인_응답 = 로그인(EXPECTED_KAKAO_TOKEN_RESPONSE, EXPECTED_KAKAO_MEMBER).as(LoginResponse.class);

            var 로그아웃_결과_기대값 = new KakaoLogoutResponse(1L);
            when(kakaoOauthApiClient.logout(anyString())).thenReturn(로그아웃_결과_기대값);

            // when
            var 로그아웃_응답 = 로그아웃_API(로그인_응답.accessToken());

            // then
            HTTP_상태_코드를_검증한다(로그아웃_응답, NO_CONTENT);
        }
    }

    @Nested
    class 회원_탈퇴 {

        @Test
        void 회원탈퇴를_한다() {
            // given
            var 로그인_응답 = 로그인(EXPECTED_KAKAO_TOKEN_RESPONSE, EXPECTED_KAKAO_MEMBER).as(LoginResponse.class);

            // when
            when(kakaoOauthApiClient.logout(anyString())).thenReturn(new KakaoLogoutResponse(1L));
            var 회원탈퇴_응답 = 회원탈퇴_API(로그인_응답.accessToken());

            // then
            var 회원조회_응답 = 비회원_회원_정보_조회_요청(로그인_응답.id());
            assertAll(
                    () -> HTTP_상태_코드를_검증한다(회원조회_응답, BAD_REQUEST),
                    () -> assertThat(회원조회_응답.body().as(ErrorResponse.class).code()).isEqualTo(DELETED_USER_EXCEPTION.name()),
                    () -> HTTP_상태_코드를_검증한다(회원탈퇴_응답, NO_CONTENT)
            );
        }
    }
}
