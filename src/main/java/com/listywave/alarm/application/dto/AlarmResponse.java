package com.listywave.alarm.application.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmResponse {

    private Long id;
    private Long sendUserId;
    private String nickname;
    private String profileImageUrl;
    private Long listId;
    private Long commentId;
    private String listTitle;
    private String type;
    private boolean isChecked;
    private LocalDateTime createdDate;
}
