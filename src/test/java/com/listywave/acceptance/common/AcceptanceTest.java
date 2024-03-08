package com.listywave.acceptance.common;

import static com.listywave.acceptance.common.CommonAcceptanceSteps.given;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.listywave.auth.application.domain.JwtManager;
import com.listywave.auth.infra.kakao.KakaoOauthApiClient;
import com.listywave.auth.infra.kakao.response.KakaoLogoutResponse;
import com.listywave.auth.infra.kakao.response.KakaoMember;
import com.listywave.auth.infra.kakao.response.KakaoTokenResponse;
import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.repository.CollaboratorRepository;
import com.listywave.collection.application.domain.Collect;
import com.listywave.collection.repository.CollectionRepository;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.repository.ItemRepository;
import com.listywave.list.repository.list.ListRepository;
import com.listywave.user.application.domain.Follow;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.follow.FollowRepository;
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
    @Autowired
    protected CollectionRepository collectionRepository;
    @Autowired
    protected CollaboratorRepository collaboratorRepository;
    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected FollowRepository followRepository;

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

    protected ExtractableResponse<Response> 로그인을_시도한다() {
        KakaoTokenResponse kakaoTokenResponse = new KakaoTokenResponse("Bearer", "AccessToken", Integer.MAX_VALUE, "RefreshToken", Integer.MAX_VALUE, "email");
        when(kakaoOauthApiClient.requestToken(any()))
                .thenReturn(kakaoTokenResponse);

        KakaoMember kakaoMember = new KakaoMember(1L, new KakaoMember.KakaoAccount(true, true, true, "listywave@kakao.com"));
        when(kakaoOauthApiClient.fetchKakaoMember(anyString()))
                .thenReturn(kakaoMember);

        return given()
                .queryParam("code", "AuthCode")
                .when().get("/auth/redirect/kakao")
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> 회원_탈퇴(String accessToken) {
        when(kakaoOauthApiClient.logout(anyString()))
                .thenReturn(new KakaoLogoutResponse(55L));

        return given()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .when().delete("/withdraw")
                .then().log().all()
                .extract();
    }

    protected String 액세스_토큰을_발급한다(User user) {
        return jwtManager.createAccessToken(user.getId());
    }

    protected ListEntity 리스트를_저장한다(ListEntity list) {
        return listRepository.save(list);
    }

    protected void 리스트를_모두_저장한다(List<ListEntity> lists) {
        listRepository.saveAll(lists);
    }

    protected void 콜라보레이터를_저장한다(User user, ListEntity list) {
        collaboratorRepository.save(Collaborator.init(user, list));
    }

    protected Collect 콜렉트를_저장한다(User user, ListEntity list) {
        return collectionRepository.save(new Collect(list, user.getId()));
    }

    protected void 팔로우를_저장한다(User followerUser, User followingUser) {
        followRepository.save(new Follow(followingUser, followerUser));
    }
}
