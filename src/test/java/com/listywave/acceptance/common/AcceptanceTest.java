package com.listywave.acceptance.common;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.listywave.acceptance.auth.AuthAcceptanceTestHelper;
import com.listywave.auth.application.domain.JwtManager;
import com.listywave.auth.infra.kakao.KakaoOauthApiClient;
import com.listywave.auth.infra.kakao.response.KakaoLogoutResponse;
import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@TestInstance(PER_CLASS)
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    @LocalServerPort
    protected int port;
    private DatabaseCleaner databaseCleaner;

    @MockBean
    protected KakaoOauthApiClient kakaoOauthApiClient;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ListRepository listRepository;
    @Autowired
    protected JwtManager jwtManager;

    @BeforeAll
    void beforeAll(@Autowired JdbcTemplate jdbcTemplate) {
        this.databaseCleaner = new DatabaseCleaner(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.clean();
    }

    protected User 회원을_저장한다(User user) {
        return userRepository.save(user);
    }

    protected ExtractableResponse<Response> 로그인(KakaoTokenResponse expectedKakaoTokenResponse, KakaoMember expectedKakaoMember) {
        when(kakaoOauthApiClient.requestToken(any()))
                .thenReturn(expectedKakaoTokenResponse);
        when(kakaoOauthApiClient.fetchKakaoMember(anyString()))
                .thenReturn(expectedKakaoMember);
        return AuthAcceptanceTestHelper.로그인_API();
    }

    protected ExtractableResponse<Response> 회원_탈퇴(KakaoLogoutResponse expectedKakaoLogoutResponse, String accessToken) {
        when(kakaoOauthApiClient.logout(anyString()))
                .thenReturn(expectedKakaoLogoutResponse);
        return AuthAcceptanceTestHelper.회원탈퇴_API(accessToken);
    }

    protected String 액세스_토큰을_발급한다(User user) {
        return jwtManager.createAccessToken(user.getId());
    }

    protected void 리스트를_모두_저장한다(List<ListEntity> lists) {
        listRepository.saveAll(lists);
    }

    protected ListEntity 리스트를_저장한다(ListEntity list) {
        return listRepository.save(list);
    }
}
