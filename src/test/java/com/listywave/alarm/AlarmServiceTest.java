package com.listywave.alarm;

import static com.listywave.alarm.application.domain.AlarmType.COLLECT;
import static com.listywave.alarm.application.domain.AlarmType.COMMENT;
import static com.listywave.alarm.application.domain.AlarmType.FOLLOW;
import static com.listywave.alarm.application.domain.AlarmType.MENTION;
import static com.listywave.alarm.application.domain.AlarmType.REPLY;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.alarm.application.dto.AlarmCheckResponse;
import com.listywave.alarm.application.dto.AlarmFindResponse;
import com.listywave.common.IntegrationTest;
import com.listywave.list.application.dto.ReplyDeleteCommand;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AlarmServiceTest extends IntegrationTest {

    @Nested
    class 알람_생성 {

        @Nested
        class 댓글_작성 {

            @Test
            void 리스트에_댓글_작성_시_작성자에게_알람이_생성된다() {
                // when
                Long commentId = commentService.create(list.getId(), js.getId(), "댓글", EMPTY_LIST).id();

                // then
                List<AlarmFindResponse> result = alarmService.findAllBy(dh.getId());
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
                // when
                commentService.create(list.getId(), dh.getId(), "댓글", EMPTY_LIST);

                // then
                assertThat(alarmService.findAllBy(dh.getId())).isEmpty();
            }

            @Test
            void 댓글_작성시_언급할_경우_언급_대상에게_알람이_생성된다() {
                // when
                commentService.create(list.getId(), js.getId(), "정수의 댓글", List.of(ej.getId(), sy.getId()));

                // then
                List<AlarmFindResponse> ejAlarms = alarmService.findAllBy(ej.getId());
                List<AlarmFindResponse> syAlarms = alarmService.findAllBy(sy.getId());
                assertAll(
                        () -> assertThat(ejAlarms).hasSize(1),
                        () -> assertThat(syAlarms).hasSize(1),
                        () -> assertThat(ejAlarms.get(0).type())
                                .isEqualTo(syAlarms.get(0).type())
                                .isEqualTo(MENTION.name()),
                        () -> assertThat(ejAlarms.get(0).comment().content())
                                .isEqualTo(syAlarms.get(0).comment().content())
                                .isEqualTo("정수의 댓글")
                );
            }

            @Test
            void 본인을_언급한_경우_알림이_생성되지_않는다() {
                // when
                commentService.create(list.getId(), js.getId(), "내가 쓴 댓글~", List.of(js.getId()));

                // then
                List<AlarmFindResponse> result = alarmService.findAllBy(js.getId());
                assertThat(result).isEmpty();
            }
        }

        @Nested
        class 답글_작성 {

            @Test
            void 답글을_작성하면_댓글_작성자에게_알람이_생성된다() {
                // when
                Long commentId = commentService.create(list.getId(), js.getId(), "댓글이용", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, ej.getId(), "답글이용", List.of(js.getId()));

                // then
                List<AlarmFindResponse> result = alarmService.findAllBy(js.getId());
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
                // when
                Long commentId = commentService.create(list.getId(), js.getId(), "정수가 남긴 댓글", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, js.getId(), "정수가 쓴 답글", EMPTY_LIST);

                // then
                List<AlarmFindResponse> result = alarmService.findAllBy(js.getId());
                assertThat(result).isEmpty();
            }

            @Test
            void 아무도_멘션을_하지_않은_채_답글을_달면_해당_댓글에_답글을_작성한_모든_사람들에게_알람이_생성된다() {
                // when
                Long commentId = commentService.create(list.getId(), js.getId(), "정수의 댓글", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, ej.getId(), "유진의 답글", List.of(js.getId()));
                replyService.create(list.getId(), commentId, dh.getId(), "동호의 답글", List.of(js.getId()));
                replyService.create(list.getId(), commentId, sy.getId(), "서영의 답글", EMPTY_LIST);

                // then
                List<AlarmFindResponse> dhAlarms = alarmService.findAllBy(dh.getId());
                List<AlarmFindResponse> jsAlarms = alarmService.findAllBy(js.getId());
                List<AlarmFindResponse> ejAlarms = alarmService.findAllBy(ej.getId());
                List<AlarmFindResponse> syAlarms = alarmService.findAllBy(sy.getId());

                assertAll(
                        () -> assertThat(dhAlarms).hasSize(2),
                        () -> assertThat(jsAlarms).hasSize(3),
                        () -> assertThat(ejAlarms).hasSize(1),
                        () -> assertThat(syAlarms).hasSize(0)
                );
            }

            @Test
            void 답글_작성_시_제_3의_유저를_언급하면_대상에게_알람이_생성된다() {
                // when
                Long commentId = commentService.create(list.getId(), js.getId(), "정수의 댓글", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, ej.getId(), "이 댓글 진짜 웃기지 않나요?ㅋㅋㅋ", List.of(sy.getId()));

                // then
                List<AlarmFindResponse> dhAlarms = alarmService.findAllBy(dh.getId());
                List<AlarmFindResponse> jsAlarms = alarmService.findAllBy(js.getId());
                List<AlarmFindResponse> ejAlarms = alarmService.findAllBy(ej.getId());
                List<AlarmFindResponse> syAlarms = alarmService.findAllBy(sy.getId());
                assertAll(
                        () -> assertThat(dhAlarms).hasSize(1),
                        () -> assertThat(dhAlarms.get(0).type()).isEqualTo(COMMENT.name()),
                        () -> assertThat(jsAlarms).hasSize(1),
                        () -> assertThat(jsAlarms.get(0).type()).isEqualTo(REPLY.name()),
                        () -> assertThat(ejAlarms).hasSize(0),
                        () -> assertThat(syAlarms).hasSize(1),
                        () -> assertThat(syAlarms.get(0).type()).isEqualTo(MENTION.name())
                );
            }

            @Test
            void 본인을_언급했다면_알람이_생성되지_않는다() {
                // when
                Long commentId = commentService.create(list.getId(), js.getId(), "정수의 댓글", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, ej.getId(), "나중에 다시 볼 댓글~", List.of(ej.getId()));

                // then
                List<AlarmFindResponse> result = alarmService.findAllBy(ej.getId());
                assertThat(result).isEmpty();
            }
        }

        @Nested
        class 우선순위에_따른_알람_생성 {

            @Test
            void 댓글을_작성할_때_리스트_작성자를_멘션하면_우선순위에_의해_댓글_알람만_생성된다() {
                // when
                commentService.create(list.getId(), js.getId(), "이 리스트 진짜 재밌네요~!", List.of(dh.getId()));

                // then
                List<AlarmFindResponse> result = alarmService.findAllBy(dh.getId());
                assertAll(
                        () -> assertThat(result).hasSize(1),
                        () -> assertThat(result.get(0).type()).isEqualTo(COMMENT.name())
                );
            }

            @Test
            void 정수가_동호의_리스트에_작성된_유진의_댓글에_답글을_남기며_서영을_언급하는_경우() {
                // when
                Long commentId = commentService.create(list.getId(), ej.getId(), "유진이 댓글 작성", EMPTY_LIST).id();
                replyService.create(list.getId(), commentId, js.getId(), "나 정순데 서영님 언급하면서 유진님 댓글에 답글쓴다.", List.of(sy.getId()));

                // then
                List<AlarmFindResponse> dhAlarms = alarmService.findAllBy(dh.getId());
                List<AlarmFindResponse> jsAlarms = alarmService.findAllBy(js.getId());
                List<AlarmFindResponse> ejAlarms = alarmService.findAllBy(ej.getId());
                List<AlarmFindResponse> syAlarms = alarmService.findAllBy(sy.getId());

                assertAll(
                        () -> assertThat(dhAlarms).hasSize(1),
                        () -> assertThat(jsAlarms).hasSize(0),
                        () -> assertThat(ejAlarms).hasSize(1),
                        () -> assertThat(syAlarms).hasSize(1),
                        () -> {
                            AlarmFindResponse dhAlarm = dhAlarms.get(0);
                            assertThat(dhAlarm.type()).isEqualTo(COMMENT.name());
                            assertThat(dhAlarm.comment().content()).isEqualTo("유진이 댓글 작성");
                        },
                        () -> {
                            AlarmFindResponse ejAlarm = ejAlarms.get(0);
                            assertThat(ejAlarm.type()).isEqualTo(REPLY.name());
                            assertThat(ejAlarm.reply().content()).isEqualTo("나 정순데 서영님 언급하면서 유진님 댓글에 답글쓴다.");
                        },
                        () -> {
                            AlarmFindResponse syAlarm = syAlarms.get(0);
                            assertThat(syAlarm.type()).isEqualTo(MENTION.name());
                            assertThat(syAlarm.reply().content()).isEqualTo("나 정순데 서영님 언급하면서 유진님 댓글에 답글쓴다.");
                        }
                );
            }
        }

        @Nested
        class 콜렉트 {

            @Test
            void 누군가_내_리스트를_콜렉트하면_알람이_생성된다() {
                // when
                Long folderId = folderService.create(js.getId(), "동호의 리스트 콜렉트용").folderId();
                collectionService.collectOrCancel(list.getId(), folderId, js.getId());

                // then
                List<AlarmFindResponse> result = alarmService.findAllBy(dh.getId());
                assertThat(result).hasSize(1);
                assertThat(result.get(0).type()).isEqualTo(COLLECT.name());
                assertThat(result.get(0).sendUser().id()).isEqualTo(js.getId());
            }
        }

        @Nested
        class 팔로우 {

            @Test
            void 누군가_나를_팔로우하면_알람이_생성된다() {
                // when
                userService.follow(js.getId(), dh.getId());

                // then
                List<AlarmFindResponse> result = alarmService.findAllBy(dh.getId());
                assertThat(result).hasSize(1);
                assertThat(result.get(0).type()).isEqualTo(FOLLOW.name());
                assertThat(result.get(0).sendUser().id()).isEqualTo(js.getId());
            }
        }
    }

    @Nested
    class 알람_조회 {

        @Test
        void 알람은_최신순으로_정렬되어_응답한다() {
            // when
            commentService.create(list.getId(), js.getId(), "정수 댓글", EMPTY_LIST);
            commentService.create(list.getId(), ej.getId(), "유진 댓글", EMPTY_LIST);
            commentService.create(list.getId(), sy.getId(), "서영 댓글", EMPTY_LIST);

            // when
            //commit();

            // then
            List<AlarmFindResponse> result = alarmService.findAllBy(dh.getId());
            assertThat(result.get(0).sendUser().id()).isEqualTo(sy.getId());
            assertThat(result.get(1).sendUser().id()).isEqualTo(ej.getId());
            assertThat(result.get(2).sendUser().id()).isEqualTo(js.getId());
        }
    }

    @Nested
    class 알람_읽기 {

        @Test
        void 알람을_읽기_처리한다() {
            // when
            commentService.create(list.getId(), js.getId(), "댓글~!", EMPTY_LIST);
            //commit();

            // when
            AlarmFindResponse alarm = alarmService.findAllBy(dh.getId()).get(0);
            assertThat(alarm.isChecked()).isFalse();
            alarmService.check(alarm.id());

            // then
            AlarmFindResponse result = alarmService.findAllBy(dh.getId()).get(0);
            assertThat(result.isChecked()).isTrue();
        }

        @Test
        void 신규_알람_조회_시에_읽지_않은_알람이_있는_경우_true를_반환한다() {
            // when
            commentService.create(list.getId(), js.getId(), "댓글~!", EMPTY_LIST);
            //commit();

            AlarmFindResponse dhAlarm = alarmService.findAllBy(dh.getId()).get(0);
            alarmService.check(dhAlarm.id());

            // when
            AlarmCheckResponse result = alarmService.isAllChecked(dh.getId());

            // then
            assertThat(result.isAllChecked()).isTrue();
        }

        @Test
        void 신규_알람_조회_시에_읽지_않은_알람이_없는_경우_false를_반환한다() {
            // when
            commentService.create(list.getId(), js.getId(), "댓글~!", EMPTY_LIST);
            //commit();

            // when
            AlarmCheckResponse result = alarmService.isAllChecked(dh.getId());

            // then
            assertThat(result.isAllChecked()).isFalse();
        }

        @Test
        void 존재하는_모든_알람을_읽음_처리한다() {
            // when
            commentService.create(list.getId(), js.getId(), "정수 댓글", EMPTY_LIST);
            commentService.create(list.getId(), ej.getId(), "유진 댓글", EMPTY_LIST);
            commentService.create(list.getId(), sy.getId(), "서영 댓글", EMPTY_LIST);

            //commit();

            assertThat(alarmService.isAllChecked(dh.getId()).isAllChecked()).isFalse();

            // when
            alarmService.checkAll(dh.getId());

            // then
            assertThat(alarmService.isAllChecked(dh.getId()).isAllChecked()).isTrue();
        }
    }

    @Nested
    class 알람_삭제 {

        @Test
        @Disabled
        void _30일이_지난_알람은_자동_삭제된다() {
            // when

            // when

            // then
        }

        @Test
        void 댓글이_삭제될_경우_관련_알람도_모두_삭제된다() {
            // when
            Long commentId = commentService.create(list.getId(), js.getId(), "정수 댓글!", List.of(ej.getId())).id();
            //commit();
            assertThat(alarmService.findAllBy(dh.getId())).hasSize(1);
            assertThat(alarmService.findAllBy(ej.getId())).hasSize(1);

            // when
            commentService.delete(list.getId(), commentId, js.getId());

            // then
            assertThat(alarmService.findAllBy(dh.getId())).isEmpty();
            assertThat(alarmService.findAllBy(ej.getId())).isEmpty();
        }

        @Test
        void 답글이_삭제될_경우_관련_알람도_모두_삭제된다() {
            // when
            Long commentId = commentService.create(list.getId(), js.getId(), "정수 댓글!", EMPTY_LIST).id();
            Long replyId = replyService.create(list.getId(), commentId, ej.getId(), "답글이용", List.of(js.getId())).id();
            //commit();

            // when
            replyService.delete(new ReplyDeleteCommand(list.getId(), commentId, replyId), ej.getId());

            // then
            assertAll(
                    () -> assertThat(alarmService.findAllBy(dh.getId())).hasSize(1),
                    () -> assertThat(alarmService.findAllBy(js.getId())).isEmpty()
            );
        }

        @Test
        void 댓글에서_언급되어_생성된_알람은_댓글이_삭제되면_함께_삭제된다() {
            // when
            Long commentId = commentService.create(list.getId(), js.getId(), "정수 댓글", List.of(ej.getId())).id();
            commentService.create(list.getId(), ej.getId(), "유진 댓글", List.of(sy.getId()));
            commentService.create(list.getId(), sy.getId(), "서영 댓글", EMPTY_LIST);

            //commit();
            assertThat(alarmService.findAllBy(dh.getId())).hasSize(3);
            assertThat(alarmService.findAllBy(js.getId())).isEmpty();
            assertThat(alarmService.findAllBy(ej.getId())).hasSize(1);
            assertThat(alarmService.findAllBy(sy.getId())).hasSize(1);

            // when
            commentService.delete(list.getId(), commentId, js.getId());

            // then
            assertThat(alarmService.findAllBy(dh.getId())).hasSize(2);
            assertThat(alarmService.findAllBy(js.getId())).isEmpty();
            assertThat(alarmService.findAllBy(ej.getId())).isEmpty();
            assertThat(alarmService.findAllBy(sy.getId())).hasSize(1);
        }

        @Test
        void 답글에서_언급되어_생성된_알람은_답글이_삭제되면_함께_삭제된다() {
            // when
            commentService.create(list.getId(), js.getId(), "ㅋㅋ", EMPTY_LIST);
            Long commentId = commentService.create(list.getId(), ej.getId(), "굿", List.of(dh.getId())).id();
            replyService.create(list.getId(), commentId, js.getId(), "1", List.of(ej.getId()));
            replyService.create(list.getId(), commentId, js.getId(), "2", List.of(ej.getId()));
            Long replyId = replyService.create(list.getId(), commentId, sy.getId(), "웃겨요", List.of(ej.getId(), dh.getId())).id();

            //commit();

            assertThat(alarmService.findAllBy(dh.getId())).hasSize(3);
            assertThat(alarmService.findAllBy(js.getId())).isEmpty();
            assertThat(alarmService.findAllBy(ej.getId())).hasSize(3);
            assertThat(alarmService.findAllBy(sy.getId())).isEmpty();

            // when
            replyService.delete(new ReplyDeleteCommand(list.getId(), commentId, replyId), sy.getId());

            // then
            assertAll(
                    () -> assertThat(alarmService.findAllBy(dh.getId())).hasSize(2),
                    () -> assertThat(alarmService.findAllBy(js.getId())).isEmpty(),
                    () -> assertThat(alarmService.findAllBy(ej.getId())).hasSize(2),
                    () -> assertThat(alarmService.findAllBy(sy.getId())).isEmpty()
            );
        }
    }
}
