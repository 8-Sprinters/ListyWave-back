package com.listywave.acceptance.collection;

import static com.listywave.acceptance.collection.CollectionAcceptanceTestHelper.나의_콜렉션_조회_API_호출;
import static com.listywave.acceptance.collection.CollectionAcceptanceTestHelper.콜렉트_또는_콜렉트취소_API_호출;
import static com.listywave.acceptance.common.CommonAcceptanceHelper.HTTP_상태_코드를_검증한다;
import static com.listywave.acceptance.folder.FolderAcceptanceTestHelper.폴더_생성_API_호출;
import static com.listywave.acceptance.folder.FolderAcceptanceTestHelper.폴더_생성_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.가장_좋아하는_견종_TOP3_생성_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트_저장_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.회원용_리스트_상세_조회_API_호출;
import static com.listywave.list.fixture.ListFixture.지정된_개수만큼_리스트를_생성한다;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.collection.application.dto.CollectionResponse;
import com.listywave.collection.application.dto.CollectionResponse.CollectionListsResponse;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.dto.response.ListCreateResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("콜렉션 관련 인수테스트")
public class CollectionAcceptanceTest extends AcceptanceTest {

    @Test
    void 타인의_리스트를_성공적으로_콜렉트한다() {
        // given
        var 동호 = 회원을_저장한다(동호());
        var 정수 = 회원을_저장한다(정수());
        var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        var 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
        var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());
        리스트_저장_API_호출(리스트_생성_요청_데이터, 정수_엑세스_토큰).as(ListCreateResponse.class);

        var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
        폴더_생성_API_호출(동호_엑세스_토큰, 폴더_생성_요청_데이터);

        // when
        var 응답 = 콜렉트_또는_콜렉트취소_API_호출(동호_엑세스_토큰, 1L, 1L);
        var 정수_리스트_콜렉트수 = 회원용_리스트_상세_조회_API_호출(정수_엑세스_토큰, 1L).collectCount();

        // then
        HTTP_상태_코드를_검증한다(응답, NO_CONTENT);
        assertThat(정수_리스트_콜렉트수).isOne();
    }

    @Test
    void 나의_리스트는_콜렉트할_수_없다() {
        // given
        var 정수 = 회원을_저장한다(정수());
        var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());
        리스트_저장_API_호출(리스트_생성_요청_데이터, 정수_엑세스_토큰);

        var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
        폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터);

        // when
        var 응답 = 콜렉트_또는_콜렉트취소_API_호출(정수_엑세스_토큰, 1L, 1L);
        var 정수_리스트_콜렉트수 = 회원용_리스트_상세_조회_API_호출(정수_엑세스_토큰, 1L).collectCount();

        // then
        HTTP_상태_코드를_검증한다(응답, FORBIDDEN);
        assertThat(정수_리스트_콜렉트수).isZero();
    }

    @Test
    void 콜렉션에_담긴_리스트를_콜렉트_취소한다() {
        // given
        var 동호 = 회원을_저장한다(동호());
        var 정수 = 회원을_저장한다(정수());
        var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        var 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
        var 리스트_생성_요청_데이터 = 가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of());
        리스트_저장_API_호출(리스트_생성_요청_데이터, 정수_엑세스_토큰).as(ListCreateResponse.class);

        var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
        폴더_생성_API_호출(동호_엑세스_토큰, 폴더_생성_요청_데이터);

        // when
        var 콜렉트_하기_응답 = 콜렉트_또는_콜렉트취소_API_호출(동호_엑세스_토큰, 1L, 1L);
        var 콜렉트_취소_응답 = 콜렉트_또는_콜렉트취소_API_호출(동호_엑세스_토큰, 1L, 1L);
        var 정수_리스트_콜렉트수 = 회원용_리스트_상세_조회_API_호출(정수_엑세스_토큰, 1L).collectCount();

        // then
        HTTP_상태_코드를_검증한다(콜렉트_하기_응답, NO_CONTENT);
        HTTP_상태_코드를_검증한다(콜렉트_취소_응답, NO_CONTENT);
        assertThat(정수_리스트_콜렉트수).isEqualTo(0);
    }

    @Test
    void 나의_콜렉션을_조회한다() {
        // given
        var 동호 = 회원을_저장한다(동호());
        var 정수 = 회원을_저장한다(정수());
        var 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        var 생성한_리스트 = 지정된_개수만큼_리스트를_생성한다(동호, 2);
        리스트를_모두_저장한다(생성한_리스트);

        var 폴더_생성_요청_데이터 = 폴더_생성_요청_데이터("맛집");
        폴더_생성_API_호출(정수_엑세스_토큰, 폴더_생성_요청_데이터);

        콜렉트_또는_콜렉트취소_API_호출(정수_엑세스_토큰, 1L, 1L);
        콜렉트_또는_콜렉트취소_API_호출(정수_엑세스_토큰, 2L, 1L);

        // when
        var 결과 = 나의_콜렉션_조회_API_호출(정수_엑세스_토큰, 1L)
                .as(CollectionResponse.class)
                .collectionLists().stream()
                .map(CollectionListsResponse::list)
                .collect(Collectors.toList());

        // then
        assertThat(결과).usingRecursiveComparison()
                .comparingOnlyFields("id")
                .isEqualTo(생성한_리스트.stream().map(ListEntity::getId).toList());
    }
}
