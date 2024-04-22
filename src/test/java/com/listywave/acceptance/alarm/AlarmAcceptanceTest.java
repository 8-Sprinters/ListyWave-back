package com.listywave.acceptance.alarm;

import static com.listywave.acceptance.alarm.AlarmAcceptanceTestHelper.알람_읽기_API_호출;
import static com.listywave.acceptance.alarm.AlarmAcceptanceTestHelper.알람_전체_읽기_API_호출;
import static com.listywave.acceptance.alarm.AlarmAcceptanceTestHelper.알람_조회_API_호출;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.n개의_댓글_생성_요청;
import static com.listywave.acceptance.comment.CommentAcceptanceTestHelper.댓글_저장_API_호출;
import static com.listywave.acceptance.follow.FollowAcceptanceTestHelper.팔로우_요청;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.리스트_저장_API_호출;
import static com.listywave.acceptance.list.ListAcceptanceTestHelper.좋아하는_견종_TOP3_생성_요청_데이터;
import static com.listywave.acceptance.reply.ReplyAcceptanceTestHelper.답글_등록_API_호출;
import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.유진;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.acceptance.common.AcceptanceTest;
import com.listywave.alarm.application.dto.AlarmListResponse;
import com.listywave.alarm.application.dto.FindAlarmResponse;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.presentation.dto.request.ListCreateRequest;
import com.listywave.list.presentation.dto.request.ReplyCreateRequest;
import com.listywave.list.presentation.dto.request.comment.CommentCreateRequest;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("알람 관련 인수테스트")
public class AlarmAcceptanceTest extends AcceptanceTest {

    @Test
    void 리스트_생성시_콜라보레이터_지정_당한_사람들에게_알람이_간다() {
        // given
        User 정수 = 회원을_저장한다(정수());
        User 동호 = 회원을_저장한다(동호());
        User 유진 = 회원을_저장한다(유진());
        String 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        String 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
        String 유진_엑세스_토큰 = 액세스_토큰을_발급한다(유진);
        ListCreateRequest listCreateRequest = 좋아하는_견종_TOP3_생성_요청_데이터(List.of(동호.getId(), 유진.getId()));
        리스트_저장_API_호출(listCreateRequest, 정수_엑세스_토큰);

        // when
        AlarmListResponse 동호_알람_조회_결과 = 알람_조회_API_호출(동호_엑세스_토큰).as(AlarmListResponse.class);
        AlarmListResponse 유진_알람_조회_결과 = 알람_조회_API_호출(유진_엑세스_토큰).as(AlarmListResponse.class);

        // then
        assertAll(
                () -> assertThat(동호_알람_조회_결과.alarmList()).hasSize(1),
                () -> assertThat(동호_알람_조회_결과.alarmList().get(0))
                        .extracting(FindAlarmResponse::getType, FindAlarmResponse::getSendUserId)
                        .containsExactly("COLLABORATOR", 정수.getId()),
                () -> assertThat(유진_알람_조회_결과.alarmList()).hasSize(1),
                () -> assertThat(유진_알람_조회_결과.alarmList().get(0))
                        .extracting(FindAlarmResponse::getType, FindAlarmResponse::getSendUserId)
                        .containsExactly("COLLABORATOR", 정수.getId())
        );
    }

    @Test
    void 팔로우_요청시_팔로우_요청_당한_사람에게_알람이_간다() {
        // given
        User 정수 = 회원을_저장한다(정수());
        User 동호 = 회원을_저장한다(동호());
        String 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        String 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
        팔로우_요청(정수_엑세스_토큰, 동호.getId());

        // when
        AlarmListResponse 동호_알람_조회_결과 = 알람_조회_API_호출(동호_엑세스_토큰).as(AlarmListResponse.class);

        // then
        assertThat(동호_알람_조회_결과.alarmList()).hasSize(1);
        assertThat(동호_알람_조회_결과.alarmList().get(0))
                .extracting(FindAlarmResponse::getType, FindAlarmResponse::getSendUserId)
                .containsExactly("FOLLOW", 정수.getId());
    }

    @Test
    void 댓글_작성시_게시글_작성자에게_알람이_간다() {
        // given
        User 정수 = 회원을_저장한다(정수());
        User 동호 = 회원을_저장한다(동호());
        String 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        String 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
        ListEntity 정수_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(정수, List.of()));
        List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(1);
        댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_엑세스_토큰, 정수_리스트.getId(), 댓글_생성요청));

        // when
        AlarmListResponse 정수_알람_조회_결과 = 알람_조회_API_호출(정수_엑세스_토큰).as(AlarmListResponse.class);

        // then
        assertThat(정수_알람_조회_결과.alarmList()).hasSize(1);
        assertThat(정수_알람_조회_결과.alarmList().get(0))
                .extracting(FindAlarmResponse::getType, FindAlarmResponse::getSendUserId)
                .containsExactly("COMMENT", 동호.getId());
    }

    @Test
    void 게시글_작성자_본인_글에_댓글_작성시_알람이_가지_않는다() {
        // given
        User 정수 = 회원을_저장한다(정수());
        String 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        ListEntity 정수_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(정수, List.of()));
        List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(1);
        댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(정수_엑세스_토큰, 정수_리스트.getId(), 댓글_생성요청));

        // when
        AlarmListResponse 정수_알람_조회_결과 = 알람_조회_API_호출(정수_엑세스_토큰).as(AlarmListResponse.class);

        // then
        assertThat(정수_알람_조회_결과.alarmList()).hasSize(0);
    }

    @Test
    void 답글_작성시_댓글_작성자에게_알람이_간다() {
        // given
        User 정수 = 회원을_저장한다(정수());
        User 동호 = 회원을_저장한다(동호());
        User 유진 = 회원을_저장한다(유진());
        String 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
        String 유진_엑세스_토큰 = 액세스_토큰을_발급한다(유진);

        ListEntity 정수_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(정수, List.of()));
        List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(1);
        댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_엑세스_토큰, 정수_리스트.getId(), 댓글_생성요청));

        ReplyCreateRequest 답글_생성_요청_데이터 = new ReplyCreateRequest("답글 달아요! 😀 ");
        답글_등록_API_호출(유진_엑세스_토큰, 답글_생성_요청_데이터, 정수_리스트.getId(), 1L);

        // when
        AlarmListResponse 동호_알람_조회_결과 = 알람_조회_API_호출(동호_엑세스_토큰).as(AlarmListResponse.class);

        // then
        assertThat(동호_알람_조회_결과.alarmList()).hasSize(1);
        assertThat(동호_알람_조회_결과.alarmList().get(0))
                .extracting(FindAlarmResponse::getType, FindAlarmResponse::getSendUserId)
                .containsExactly("REPLY", 유진.getId());
    }

    @Test
    void 나에게_온_알람을_읽는다() {
        // given
        User 정수 = 회원을_저장한다(정수());
        User 동호 = 회원을_저장한다(동호());
        String 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        String 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
        ListEntity 정수_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(정수, List.of()));
        List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(1);
        댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_엑세스_토큰, 정수_리스트.getId(), 댓글_생성요청));

        // when
        알람_읽기_API_호출(정수_엑세스_토큰, 1L);

        // then
        AlarmListResponse 정수_알람_조회_결과 = 알람_조회_API_호출(정수_엑세스_토큰).as(AlarmListResponse.class);

        assertThat(정수_알람_조회_결과.alarmList()).hasSize(1);
        assertThat(정수_알람_조회_결과.alarmList().get(0))
                .extracting(FindAlarmResponse::getType, FindAlarmResponse::getSendUserId, FindAlarmResponse::isChecked)
                .containsExactly("COMMENT", 동호.getId(), true);
    }

    @Test
    void 나에게_온_모든_알람을_읽는다() {
        // given
        User 정수 = 회원을_저장한다(정수());
        User 동호 = 회원을_저장한다(동호());
        String 정수_엑세스_토큰 = 액세스_토큰을_발급한다(정수);
        String 동호_엑세스_토큰 = 액세스_토큰을_발급한다(동호);
        ListEntity 정수_리스트 = 리스트를_저장한다(가장_좋아하는_견종_TOP3(정수, List.of()));

        List<CommentCreateRequest> 댓글_생성_요청들 = n개의_댓글_생성_요청(10);
        댓글_생성_요청들.forEach(댓글_생성요청 -> 댓글_저장_API_호출(동호_엑세스_토큰, 정수_리스트.getId(), 댓글_생성요청));

        // when
        알람_전체_읽기_API_호출(정수_엑세스_토큰);

        // then
        AlarmListResponse 정수_알람_조회_결과 = 알람_조회_API_호출(정수_엑세스_토큰).as(AlarmListResponse.class);

        assertThat(정수_알람_조회_결과.alarmList()).hasSize(10);
        assertThat(정수_알람_조회_결과.alarmList())
                .allMatch(alarmResponse -> alarmResponse.isChecked() == true);
    }
}
