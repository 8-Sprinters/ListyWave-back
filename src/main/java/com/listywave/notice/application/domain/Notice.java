package com.listywave.notice.application.domain;

import static com.listywave.common.exception.ErrorCode.ALREADY_SENT_ALARM_NOTICE;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Comparator.comparingInt;
import static lombok.AccessLevel.PROTECTED;

import com.listywave.admin.Admin;
import com.listywave.common.BaseEntity;
import com.listywave.common.exception.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Notice extends BaseEntity {

    @Column(name = "code", nullable = false)
    private NoticeType type;

    @Embedded
    private NoticeTitle title;

    @Embedded
    private NoticeDescription description;

    private boolean isExposed;

    private boolean didSendAlarm;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "create_admin_id")
    private Admin createAdmin;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "update_admin_id")
    private Admin lastUpdateAdmin;

    @OneToMany(mappedBy = "notice", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private final List<NoticeContent> contents = new ArrayList<>();

    public Notice(NoticeType type, NoticeTitle title, NoticeDescription description, Admin createAdmin) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.createAdmin = createAdmin;
        this.lastUpdateAdmin = createAdmin;
    }

    public void addContents(List<NoticeContent> contents) {
        this.contents.addAll(contents);
    }

    public String getFirstImageUrl() {
        Optional<String> result = contents.stream()
                .sorted(comparingInt(NoticeContent::getOrder))
                .filter(NoticeContent::hasImage)
                .map(NoticeContent::getImageUrl)
                .findFirst();
        return result.orElse(null);
    }

    public void update(
            NoticeType type,
            NoticeTitle title,
            NoticeDescription description,
            List<NoticeContent> contents,
            Admin updateAdmin
    ) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.lastUpdateAdmin = updateAdmin;

        this.contents.clear();
        this.contents.addAll(contents);
    }

    public void changeExposure(Admin updateAdmin) {
        this.isExposed = !this.isExposed;
        this.lastUpdateAdmin = updateAdmin;
    }

    public void sendAlarm() {
        if (this.didSendAlarm) {
            throw new CustomException(ALREADY_SENT_ALARM_NOTICE);
        }
        this.didSendAlarm = true;
    }
}
