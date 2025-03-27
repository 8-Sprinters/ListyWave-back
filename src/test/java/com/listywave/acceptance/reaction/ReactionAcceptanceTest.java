package com.listywave.acceptance.reaction;

import static com.listywave.acceptance.list.ListAcceptanceTestHelper.가장_좋아하는_견종_TOP3_생성_요청_데이터;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트_저장_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.비회원_리스트_상세_조회_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.회원용_리스트_상세_조회_API_호출;
import static com.listywave.acceptance.reaction.ReactionAcceptanceTestHelper.리액션_API_호출;
import static com.listywave.acceptance.reaction.ReactionAcceptanceTestHelper.리액션_요청_데이터_리스트;
import static com.listywave.acceptance.reaction.ReactionAcceptanceTestHelper.리액션_일괄_호출;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.list.application.dto.response.ListCreateResponse;
import com.listywave.list.application.dto.response.ListDetailResponse;
import com.listywave.reaction.application.domain.Reaction;
import com.listywave.reaction.presentation.dto.request.ReactionRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("리액션 관련 인수테스트")
public class ReactionAcceptanceTest extends AcceptanceTest {

    @Test
    void 리스트에_대한_리액션을_성공적으로_수행한다() {
        // given
        var 정수 = 회원을_저장한다(정수());
        var 동호 = 회원을_저장한다(동호());
        var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
        var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
        var 정수_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 정수_액세스_토큰)
                .as(ListCreateResponse.class)
                .listId();

        // when
        리액션_일괄_호출(정수_액세스_토큰, 정수_리스트_ID, 리액션_요청_데이터_리스트(Reaction.COOL, Reaction.AGREE));
        리액션_일괄_호출(동호_액세스_토큰, 정수_리스트_ID, 리액션_요청_데이터_리스트(Reaction.COOL));
        var 결과 = 회원용_리스트_상세_조회_API_호출(정수_액세스_토큰, 정수_리스트_ID);

        // then
        assertAll(
                () -> assertThat(결과.reactions().get(0).count()).isEqualTo(2),
                () -> assertThat(결과.reactions().get(0).reaction()).isEqualTo("COOL"),
                () -> assertThat(결과.reactions().get(1).count()).isEqualTo(1),
                () -> assertThat(결과.reactions().get(1).reaction()).isEqualTo("AGREE"),
                () -> assertThat(결과.reactions().get(2).count()).isEqualTo(0),
                () -> assertThat(결과.reactions().get(2).reaction()).isEqualTo("THANKS")
        );
    }

    @Test
    void 리스트에_대한_리액션을_취소한다() {
        // given
        var 정수 = 회원을_저장한다(정수());
        var 동호 = 회원을_저장한다(동호());
        var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
        var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
        var 정수_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 정수_액세스_토큰)
                .as(ListCreateResponse.class)
                .listId();

        // when
        리액션_일괄_호출(동호_액세스_토큰, 정수_리스트_ID, 리액션_요청_데이터_리스트(Reaction.COOL, Reaction.COOL));

        var 결과 = 회원용_리스트_상세_조회_API_호출(정수_액세스_토큰, 정수_리스트_ID);

        // then
        assertAll(
                () -> assertThat(결과.reactions().get(0).count()).isEqualTo(0),
                () -> assertThat(결과.reactions().get(0).reaction()).isEqualTo("COOL"),
                () -> assertThat(결과.reactions().get(1).count()).isEqualTo(0),
                () -> assertThat(결과.reactions().get(1).reaction()).isEqualTo("AGREE"),
                () -> assertThat(결과.reactions().get(2).count()).isEqualTo(0),
                () -> assertThat(결과.reactions().get(2).reaction()).isEqualTo("THANKS")
        );
    }

    @Test
    void 리스트_생성자가_아닌_사용자_및_비회원은_리액션_수를_볼_수_없다() {
        // given
        var 정수 = 회원을_저장한다(정수());
        var 동호 = 회원을_저장한다(동호());
        var 정수_액세스_토큰 = 액세스_토큰을_발급한다(정수);
        var 동호_액세스_토큰 = 액세스_토큰을_발급한다(동호);
        var 정수_리스트_ID = 리스트_저장_API_호출(가장_좋아하는_견종_TOP3_생성_요청_데이터(List.of()), 정수_액세스_토큰)
                .as(ListCreateResponse.class)
                .listId();
        var 동호_리액션_요청_데이터1 = new ReactionRequest(Reaction.COOL);

        // when
        리액션_API_호출(동호_액세스_토큰, 정수_리스트_ID, 동호_리액션_요청_데이터1);
        var 비회원_상세_결과 = 비회원_리스트_상세_조회_API_호출(정수_리스트_ID).as(ListDetailResponse.class);
        var 비소유자_상세_결과 = 회원용_리스트_상세_조회_API_호출(동호_액세스_토큰, 정수_리스트_ID);

        // then
        assertAll(
                () -> assertThat(비회원_상세_결과.reactions().get(0).count()).isNull(),
                () -> assertThat(비회원_상세_결과.reactions().get(0).count()).isNull(),
                () -> assertThat(비회원_상세_결과.reactions().get(0).count()).isNull(),
                () -> assertThat(비소유자_상세_결과.reactions().get(0).count()).isNull(),
                () -> assertThat(비소유자_상세_결과.reactions().get(0).count()).isNull(),
                () -> assertThat(비소유자_상세_결과.reactions().get(0).count()).isNull()
        );
    }
}
