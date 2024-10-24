package com.listywave.alarm.application.domain;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.TemporalType.TIMESTAMP;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.reply.Reply;
import com.listywave.user.application.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Alarm {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "send_user_id", nullable = false)
    private User sendUser;

    @Column(nullable = false)
    private Long receiveUserId;

    @ManyToOne(fetch = LAZY, cascade = REMOVE)
    @JoinColumn(name = "list_id", nullable = true, foreignKey = @ForeignKey(name = "alarm_list_fk"))
    private ListEntity list;

    @ManyToOne(fetch = LAZY, cascade = REMOVE)
    @JoinColumn(name = "comment_id", nullable = true, foreignKey = @ForeignKey(name = "alarm_comment_fk"))
    private Comment comment;

    @ManyToOne(fetch = LAZY, cascade = REMOVE)
    @JoinColumn(name = "reply_id", nullable = true, foreignKey = @ForeignKey(name = "alarm_reply_fk"))
    private Reply reply;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private AlarmType type;

    @Column(nullable = false)
    private boolean isChecked;

    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(updatable = false)
    private LocalDateTime createdDate;

    public void read() {
        this.isChecked = true;
    }
}
