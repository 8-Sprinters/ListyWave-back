package com.listywave.alarm.application.service;

import static com.listywave.alarm.application.domain.AlarmType.COMMENT;
import static com.listywave.alarm.application.domain.AlarmType.MENTION;
import static com.listywave.alarm.application.domain.AlarmType.REPLY;
import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import com.listywave.alarm.application.domain.Alarm;
import com.listywave.alarm.application.domain.AlarmEvent;
import com.listywave.alarm.application.dto.AlarmCheckResponse;
import com.listywave.alarm.application.dto.AlarmFindResponse;
import com.listywave.alarm.repository.AlarmRepository;
import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.mention.Mention;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final ReplyRepository replyRepository;

    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(AlarmEvent.class)
    public void save(AlarmEvent event) {
        if (event.isToMyself()) {
            return;
        }

        if (event.alarmType().equals(REPLY)) { // 답글 타입이면
            if (event.mentions().isEmpty()) { // 아무도 멘션하지 않았다면
                // 일단 댓글 작성자에게 답글 알람 생성
                Alarm alarm = event.toEntity();

                // 댓글에 작성한 모든 답글 작성자들에게 알람을 보낸다.
                Comment comment = event.comment();
                List<Reply> replies = replyRepository.findAllByComment(comment);

                List<Alarm> alarms = replies.stream()
                        .filter(reply -> !reply.getUserId().equals(event.publisher().getId())) // 본인에게 알람이 가지 않도록 한다.
                        .map(reply -> Alarm.builder()
                                .sendUser(event.publisher())
                                .receiveUserId(reply.getUserId())
                                .list(event.list())
                                .comment(event.comment())
                                .reply(reply)
                                .type(REPLY)
                                .build())
                        .toList();
                alarmRepository.save(alarm);
                alarmRepository.saveAll(alarms);
                return;
            }

            // 댓글 작성자에게 알람 생성
            Alarm alarm = event.toEntity();

            // 멘션을 한 대상자들에게 알람 생성
            List<Mention> mentions = event.mentions();
            List<Alarm> alarms = convertMentionsToAlarms(mentions, event);
            alarmRepository.save(alarm);
            alarmRepository.saveAll(alarms);
            return;
        }

        if (event.alarmType().equals(COMMENT)) {
            if (event.mentions().isEmpty()) {
                Alarm alarm = event.toEntity();
                alarmRepository.save(alarm);
            } else {
                // 게시글 작성자에게 알람 생성
                Alarm alarm = event.toEntity();
                alarmRepository.save(alarm);

                // 언급 대상들에게 모두 알람을 보낸다.
                // 이때, 본인을 언급했다면 생성하지 않는다.
                List<Mention> mentions = event.mentions();
                List<Alarm> alarms = mentions.stream()
                        .filter(mention -> !mention.getUser().getId().equals(event.listenerId())) // 언급의 대상이 게시글 작성자인 경우 제외
                        .filter(mention -> !mention.getUser().getId().equals(event.publisher().getId())) // 언급의 대상이 본인인 경우 제외
                        .map(mention -> Alarm.builder()
                                .sendUser(event.publisher())
                                .receiveUserId(mention.getUser().getId())
                                .list(event.list())
                                .comment(event.comment())
                                .type(MENTION)
                                .build())
                        .toList();
                alarmRepository.saveAll(alarms);
            }
        }

        // 댓글 작성
//        Alarm alarm = event.toEntity();
//        alarmRepository.save(alarm);
//        if (!event.mentions().isEmpty()) {
//            List<Mention> mentions = event.mentions();
//            List<Alarm> alarms = convertMentionsToAlarms(mentions, event);
//            alarmRepository.saveAll(alarms);
//        }
    }

    private List<Alarm> convertMentionsToAlarms(List<Mention> mentions, AlarmEvent event) {
        return mentions.stream()
                .filter(mention -> !mention.getUser().getId().equals(event.comment().getUserId())) // 언급의 대상이 댓글 작성자라면 제외 (우선순위)
                .filter(mention -> !mention.getUser().getId().equals(event.publisher().getId())) // 언급의 대상이 본인이라면 제외
                .map(mention -> Alarm.builder()
                        .sendUser(event.publisher())
                        .receiveUserId(mention.getUser().getId())
                        .list(event.list())
                        .comment(event.comment())
                        .reply(event.reply())
                        .type(REPLY)
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlarmFindResponse> findAlarms(Long userId) {
        userRepository.getById(userId);
        List<Alarm> alarms = alarmRepository.findAllByReceiveUserId(userId, LocalDateTime.now().minusDays(30));
        return AlarmFindResponse.toList(alarms);
    }

    public void readAlarm(Long alarmId, Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        Alarm alarm = alarmRepository.findAlarmByIdAndReceiveUserId(alarmId, user.getId())
                .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));
        alarm.read();
    }

    public AlarmCheckResponse checkAllAlarmsRead(Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        return new AlarmCheckResponse(alarmRepository.hasCheckedAlarmsByReceiveUserId(user.getId()));
    }

    public void readAllAlarm(Long loginUserId) {
        User user = userRepository.getById(loginUserId);
        alarmRepository.readAllAlarm(user.getId());
    }
}
