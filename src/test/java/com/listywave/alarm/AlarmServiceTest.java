package com.listywave.alarm;

import static com.listywave.alarm.application.domain.AlarmType.COMMENT;
import static com.listywave.alarm.application.domain.AlarmType.REPLY;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.alarm.application.dto.AlarmFindResponse;
import com.listywave.common.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.transaction.TestTransaction;

@Commit
public class AlarmServiceTest extends IntegrationTest {

    @AfterEach
    void tearDown() {
        alarmRepository.deleteAllInBatch();
        mentionRepository.deleteAllInBatch();
        replyRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        itemRepository.deleteAllInBatch();
        labelRepository.deleteAllInBatch();
        listRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Nested
    class 알람_생성 {

        @Nested
        class 댓글_작성 {

            @Test
            void 리스트에_댓글_작성_시_작성자에게_알람이_생성된다() {
                // given
                Long commentId = commentService.create(list.getId(), js.getId(), "댓글", EMPTY_LIST).id();

                // when
                commit();

                // then
                List<AlarmFindResponse> result = alarmService.findAlarms(dh.getId());
                assertAll(
                        () -> assertThat(result).hasSize(1),
                        () -> assertThat(result.get(0).sendUser().id()).isEqualTo(js.getId()),
                        () -> assertThat(result.get(0).type()).isEqualTo(COMMENT.name()),
                        () -> assertThat(result.get(0).list().title()).isEqualTo(list.getTitle().getValue()),
                        () -> assertThat(result.get(0).comment().id()).isEqualTo(commentId)
                );
            }

            @Test
            void 본인_리스트에_댓글_작성_시_알람이_생성되지_않는다() {
                // given
                commentService.create(list.getId(), dh.getId(), "댓글", EMPTY_LIST);

                // when
                commit();

                // then
                assertThat(alarmService.findAlarms(dh.getId())).isEmpty();
            }

            @Test
            void 댓글_작성시_언급할_경우_언급_대상에게_알람이_생성된다() {
                // given
                commentService.create(list.getId(), js.getId(), "정수의 댓글", List.of(ej.getId(), sy.getId()));

                // when
                commit();

                // then
                List<AlarmFindResponse> ejAlarms = alarmService.findAlarms(ej.getId());
                List<AlarmFindResponse> syAlarms = alarmService.findAlarms(sy.getId());
                assertThat(ejAlarms).hasSize(1);
                assertThat(syAlarms).hasSize(1);
            }

            @Test
            void 본인을_언급한_경우_알림이_생성되지_않는다() {
                // given
                commentService.create(list.getId(), js.getId(), "내가 쓴 댓글~", List.of(js.getId()));

                // when
                commit();

                // then
                List<AlarmFindResponse> result = alarmService.findAlarms(js.getId());
                assertThat(result).isEmpty();
            }
        }

        @Nested
        class 답글_작성 {

            @Test
            void 답글을_작성하면_댓글_작성자에게_알람이_생성된다() {
                // given
                Long commentId = commentService.create(list.getId(), js.getId(), "댓글이용", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, ej.getId(), "답글이용", List.of(js.getId()));

                // when
                commit();

                // then
                List<AlarmFindResponse> result = alarmService.findAlarms(js.getId());
                assertAll(
                        () -> assertThat(result).hasSize(1),
                        () -> assertThat(result.get(0).type()).isEqualTo(REPLY.name()),
                        () -> assertThat(result.get(0).sendUser().id()).isEqualTo(ej.getId()),
                        () -> assertThat(result.get(0).list().id()).isEqualTo(list.getId()),
                        () -> assertThat(result.get(0).comment().id()).isEqualTo(commentId),
                        () -> assertThat(result.get(0).reply().content()).isEqualTo("답글이용")
                );
            }

            @Test
            void 본인_댓글에_답글을_남길_경우_알림이_생성되지_않는다() {
                // given
                Long commentId = commentService.create(list.getId(), js.getId(), "정수가 남긴 댓글", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, js.getId(), "정수가 쓴 답글", EMPTY_LIST);

                // when
                commit();

                // then
                List<AlarmFindResponse> result = alarmService.findAlarms(js.getId());
                assertThat(result).isEmpty();
            }

            @Test
            void 아무도_멘션을_하지_않은_채_답글을_달면_해당_댓글에_답글을_작성한_모든_사람들에게_알람이_생성된다() {
                // given
                Long commentId = commentService.create(list.getId(), js.getId(), "정수의 댓글", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, ej.getId(), "유진의 답글", List.of(js.getId()));
                replyService.create(list.getId(), commentId, dh.getId(), "동호의 답글", List.of(js.getId()));
                replyService.create(list.getId(), commentId, sy.getId(), "서영의 답글", EMPTY_LIST);

                // when
                commit();

                // then
                List<AlarmFindResponse> dhAlarms = alarmService.findAlarms(dh.getId());
                List<AlarmFindResponse> jsAlarms = alarmService.findAlarms(js.getId());
                List<AlarmFindResponse> ejAlarms = alarmService.findAlarms(ej.getId());
                List<AlarmFindResponse> syAlarms = alarmService.findAlarms(sy.getId());

                assertAll(
                        () -> assertThat(dhAlarms).hasSize(2),
                        () -> assertThat(jsAlarms).hasSize(3),
                        () -> assertThat(ejAlarms).hasSize(1),
                        () -> assertThat(syAlarms).hasSize(0)
                );
            }

            @Test
            void 답글_작성_시_언급하면_알람이_생성된다() {
                // given
                Long commentId = commentService.create(list.getId(), js.getId(), "정수의 댓글", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, ej.getId(), "이 댓글 진짜 웃기지 않나요?ㅋㅋㅋ", List.of(sy.getId()));

                // when
                commit();

                // then
                List<AlarmFindResponse> dhAlarms = alarmService.findAlarms(dh.getId());
                List<AlarmFindResponse> jsAlarms = alarmService.findAlarms(js.getId());
                List<AlarmFindResponse> ejAlarms = alarmService.findAlarms(ej.getId());
                List<AlarmFindResponse> syAlarms = alarmService.findAlarms(sy.getId());
                assertAll(
                        () -> assertThat(dhAlarms).hasSize(1),
                        () -> assertThat(jsAlarms).hasSize(1),
                        () -> assertThat(ejAlarms).hasSize(0),
                        () -> assertThat(syAlarms).hasSize(1)
                );
            }

            @Test
            void 본인을_언급했다면_알람이_생성되지_않는다() {
                // given
                Long commentId = commentService.create(list.getId(), js.getId(), "정수의 댓글", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, ej.getId(), "나중에 다시 볼 댓글~", List.of(ej.getId()));

                // when
                commit();

                // then
                List<AlarmFindResponse> result = alarmService.findAlarms(ej.getId());
                assertThat(result).isEmpty();
            }
        }

        @Nested
        class 우선순위에_따른_알람_생성 {

            @Test
            void 댓글을_작성할_때_리스트_작성자를_멘션하면_우선순위에_의해_댓글_알람만_생성된다() {
                // given

                // when

                // then
            }

            @Test
            void 댓글을_작성하면서_제_3의_사용자를_언급하는_경우() {
                // given

                // when

                // then
            }

            @Test
            void 정수가_동호의_리스트에_작성된_유진의_댓글에_답글을_남기며_서영을_언급하는_경우() {
                // given

                // when

                // then
            }
        }
    }

    private void commit() {
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
    }
}
