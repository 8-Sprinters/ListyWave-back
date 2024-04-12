package com.listywave.acceptance.auth;

import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.로그아웃_요청;
import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.로그인_요청;
import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.카카오_로그인_페이지_요청;
import static com.listywave.acceptance.auth.AuthAcceptanceTestHelper.회원탈퇴_요청;
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
import com.listywave.auth.application.dto.LoginResult;
import com.listywave.auth.infra.kakao.response.KakaoLogoutResponse;
import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoMember.KakaoAccount;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import com.listywave.auth.presentation.dto.LoginResponse;
import com.listywave.common.exception.ErrorResponse;
import com.listywave.image.application.domain.DefaultBackgroundImages;
import com.listywave.image.application.domain.DefaultProfileImages;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("인증 관련 인수테스트")
public class AuthAcceptanceTest extends AcceptanceTest {

    private static final KakaoTokenResponse expectedKakaoTokenResponse = new KakaoTokenResponse("Bearer", "accessToken", Integer.MAX_VALUE, "refreshToken", Integer.MAX_VALUE, "email");
    private static final KakaoMember expectedKakaoMember = new KakaoMember(1L, new KakaoAccount(true, true, true, "email"));

    @Nested
    class 로그인 {

        @Test
        void 카카오_로그인_페이지를_요청한다() {
            ExtractableResponse<Response> 응답 = 카카오_로그인_페이지_요청();

            assertThat(응답.statusCode()).isEqualTo(OK.value());
        }

        @Test
        void 최초_로그인_한다() {
            // given
            KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("Bearer", "AccessToken", Integer.MAX_VALUE, "RefreshToken", Integer.MAX_VALUE, "email");
            when(kakaoOauthApiClient.requestToken(any()))
                    .thenReturn(kakaoTokenResponse);

            KakaoMember kakaoMember = new KakaoMember(1L, new KakaoAccount(true, true, true, "listywave@kakao.com"));
            when(kakaoOauthApiClient.fetchKakaoMember(anyString()))
                    .thenReturn(kakaoMember);

            // when
            ExtractableResponse<Response> response = 로그인_요청();
            LoginResult result = response.as(LoginResult.class);

            // then
            List<String> defaultProfileImages = Arrays.stream(DefaultProfileImages.values())
                    .map(DefaultProfileImages::getValue)
                    .toList();

            List<String> defaultBackgroundImages = Arrays.stream(DefaultBackgroundImages.values())
                    .map(DefaultBackgroundImages::getValue)
                    .toList();

            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(OK.value()),
                    () -> assertThat(result.isFirst()).isEqualTo(true),
                    () -> assertThat(jwtManager.readAccessToken("Bearer " + result.accessToken())).isEqualTo(1L),
                    () -> assertThat(jwtManager.readRefreshToken(result.refreshToken())).isEqualTo(1L),
                    () -> assertThat(result.profileImageUrl()).isIn(defaultProfileImages),
                    () -> assertThat(result.backgroundImageUrl()).isIn(defaultBackgroundImages),
                    () -> assertThat(result.followingCount()).isEqualTo(0),
                    () -> assertThat(result.followerCount()).isEqualTo(0)
            );
        }

        @Test
        void 로그인_시_이미_DB에_회원이_있으면_isFirst에_false를_반환한다() {
            // given
            로그인을_시도한다(expectedKakaoTokenResponse, expectedKakaoMember);

            // when
            ExtractableResponse<Response> response = 로그인을_시도한다(expectedKakaoTokenResponse, expectedKakaoMember);
            LoginResult result = response.as(LoginResult.class);

            // then
            assertThat(response.statusCode()).isEqualTo(OK.value());
            assertThat(result.isFirst()).isFalse();
        }

        @Test
        void 로그인에_성공하면_Http_Body와_Cookie에_액세스_토큰과_리프레시_토큰을_담아_응답한다() {
            // when
            ExtractableResponse<Response> 응답 = 로그인을_시도한다(expectedKakaoTokenResponse, expectedKakaoMember);
            LoginResponse body = 응답.as(LoginResponse.class);

            // then
            assertAll(
                    () -> assertThat(응답.cookie("accessToken")).isNotNull(),
                    () -> assertThat(응답.cookie("accessToken")).isNotBlank(),
                    () -> assertThat(응답.cookie("refreshToken")).isNotNull(),
                    () -> assertThat(응답.cookie("refreshToken")).isNotBlank(),
                    () -> assertThat(body.accessToken()).isNotNull(),
                    () -> assertThat(body.accessToken()).isNotBlank(),
                    () -> assertThat(body.refreshToken()).isNotNull(),
                    () -> assertThat(body.refreshToken()).isNotBlank()
            );
        }
    }

    @Nested
    class 로그아웃 {

        @Test
        void 로그아웃을_한다() {
            // given
            LoginResult 로그인_결과 = 로그인을_시도한다(expectedKakaoTokenResponse, expectedKakaoMember).as(LoginResult.class);

            KakaoLogoutResponse kakaoLogoutResponse = new KakaoLogoutResponse(1L);
            when(kakaoOauthApiClient.logout(anyString()))
                    .thenReturn(kakaoLogoutResponse);

            // when
            ExtractableResponse<Response> response = 로그아웃_요청(로그인_결과.accessToken());

            // then
            assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
        }
    }

    @Nested
    class 회원_탈퇴 {

        @Test
        void 회원탈퇴를_한다() {
            // given
            when(kakaoOauthApiClient.logout(anyString()))
                    .thenReturn(new KakaoLogoutResponse(1L));
            LoginResult 로그인_결과 = 로그인을_시도한다(expectedKakaoTokenResponse, expectedKakaoMember).as(LoginResult.class);

            // when
            ExtractableResponse<Response> response = 회원탈퇴_요청(로그인_결과.accessToken());

            // then
            assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
        }

        @Test
        void 회원탈퇴를_한_사용자는_Soft_Delete_처리_된다() {
            // given
            when(kakaoOauthApiClient.logout(anyString()))
                    .thenReturn(new KakaoLogoutResponse(1L));
            LoginResult 로그인_결과 = 로그인을_시도한다(expectedKakaoTokenResponse, expectedKakaoMember).as(LoginResult.class);

            회원탈퇴_요청(로그인_결과.accessToken());

            // when
            ExtractableResponse<Response> response = 비회원_회원_정보_조회_요청(로그인_결과.id());
            ErrorResponse result = response.as(ErrorResponse.class);

            // then
            assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
            assertThat(result.code()).isEqualTo(DELETED_USER_EXCEPTION.name());
        }
    }
}
