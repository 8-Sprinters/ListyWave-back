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
import com.listywave.common.exception.ErrorResponse;
import com.listywave.image.application.domain.DefaultBackgroundImages;
import com.listywave.image.application.domain.DefaultProfileImages;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("인증 관련 인수테스트")
public class AuthAcceptanceTest extends AcceptanceTest {

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
        로그인을_시도한다();

        // when
        ExtractableResponse<Response> response = 로그인을_시도한다();
        LoginResult result = response.as(LoginResult.class);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(result.isFirst()).isFalse();
    }

    @Test
    void 로그아웃을_한다() {
        // given
        LoginResult 로그인_결과 = 로그인을_시도한다().as(LoginResult.class);

        KakaoLogoutResponse kakaoLogoutResponse = new KakaoLogoutResponse(1L);
        when(kakaoOauthApiClient.logout(anyString()))
                .thenReturn(kakaoLogoutResponse);

        // when
        ExtractableResponse<Response> response = 로그아웃_요청(로그인_결과.accessToken());

        // then
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    @Test
    void 회원탈퇴를_한다() {
        // given
        when(kakaoOauthApiClient.logout(anyString()))
                .thenReturn(new KakaoLogoutResponse(1L));

        LoginResult 로그인_결과 = 로그인을_시도한다().as(LoginResult.class);

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

        LoginResult 로그인_결과 = 로그인을_시도한다().as(LoginResult.class);

        회원탈퇴_요청(로그인_결과.accessToken());

        // when
        ExtractableResponse<Response> response = 비회원_회원_정보_조회_요청(로그인_결과.id());
        ErrorResponse result = response.as(ErrorResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(result.code()).isEqualTo(DELETED_USER_EXCEPTION.name());
    }
}
