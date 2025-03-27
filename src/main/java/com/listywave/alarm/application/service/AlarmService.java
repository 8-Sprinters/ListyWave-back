package com.listywave.alarm.application.service;

import static com.listywave.alarm.application.domain.AlarmType.COMMENT;
import static com.listywave.alarm.application.domain.AlarmType.MENTION;
import static com.listywave.alarm.application.domain.AlarmType.NOTICE;
import static com.listywave.alarm.application.domain.AlarmType.REPLY;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import com.listywave.alarm.application.domain.Alarm;
import com.listywave.alarm.application.domain.AlarmCreateEvent;
import com.listywave.alarm.application.domain.AlarmDeleteEvent;
import com.listywave.alarm.application.dto.AlarmCheckResponse;
import com.listywave.alarm.application.dto.AlarmFindResponse;
import com.listywave.alarm.repository.AlarmRepository;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.list.repository.reply.ReplyRepository;
import com.listywave.mention.Mention;
import com.listywave.user.application.domain.User;
import com.listywave.user.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
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

    // TODO: 리팩터링
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(AlarmCreateEvent.class)
    public void save(AlarmCreateEvent event) {
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
                                .type(REPLY) // 답글 타입 ㅇㅇ
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
            List<Alarm> alarms = mentions.stream()
                    .filter(mention -> !mention.getUser().getId().equals(event.comment().getUserId())) // 언급의 대상이 댓글 작성자라면 제외 (우선순위)
                    .filter(mention -> !mention.getUser().getId().equals(event.publisher().getId())) // 언급의 대상이 본인이라면 제외
                    .map(mention -> Alarm.builder()
                            .sendUser(event.publisher())
                            .receiveUserId(mention.getUser().getId())
                            .list(event.list())
                            .comment(event.comment())
                            .reply(event.reply())
                            .type(MENTION) // 멘션으로 알람 타입 생성
                            .build())
                    .toList();
            alarmRepository.save(alarm);
            alarmRepository.saveAll(alarms);
            return;
        }

        if (event.alarmType().equals(COMMENT)) {
            if (event.mentions().isEmpty()) {
                Alarm alarm = event.toEntity();
                alarmRepository.save(alarm);
                return;
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
                return;
            }
        }

        Alarm alarm = event.toEntity();
        alarmRepository.save(alarm);
    }

    @Transactional(readOnly = true)
    public List<AlarmFindResponse> findAllBy(Long userId) {
        userRepository.getById(userId);
        List<Alarm> alarms = alarmRepository.findAllBy(userId, LocalDateTime.now().minusDays(30), NOTICE);
        return AlarmFindResponse.toList(alarms);
    }

    public void check(Long alarmId) {
        Alarm alarm = alarmRepository.getById(alarmId);
        alarm.check();
    }

    public AlarmCheckResponse isAllChecked(Long userId) {
        User user = userRepository.getById(userId);
        Boolean result = alarmRepository.isAllChecked(user.getId());
        return new AlarmCheckResponse(result);
    }

    public void checkAll(Long userId) {
        User user = userRepository.getById(userId);
        alarmRepository.checkAll(user.getId());
    }

    @EventListener(AlarmDeleteEvent.class)
    @Transactional(propagation = REQUIRED)
    public void deleteAllBy(AlarmDeleteEvent event) {
        if (event.type().equals(COMMENT)) { // 댓글을 삭제하는 경우
            Comment comment = event.comment();

            if (comment.getMentions().isEmpty()) { // 멘션이 없는 경우
                alarmRepository.deleteAllByCommentAndReceiveUserId(comment, comment.getList().getUser().getId()); // 해당 댓글로 생성된 알람 삭제
            } else {
                alarmRepository.deleteAllByCommentAndReceiveUserId(comment, comment.getList().getUser().getId()); // 해당 댓글로 생성된 알람 삭제

                // 멘션에 포함된 알람 삭제
                List<Mention> mentions = comment.getMentions();
                mentions.forEach(mention -> {
                    User receiveUser = mention.getUser();
                    Long receiveUserId = receiveUser.getId();
                    alarmRepository.deleteAllByCommentAndReceiveUserId(comment, receiveUserId);
                });
            }
        }

        if (event.type().equals(REPLY)) {
            Reply reply = event.reply();
            List<Alarm> alarms = alarmRepository.findAllByReply(reply);
            alarmRepository.deleteAll(alarms);
        }
    }
}
