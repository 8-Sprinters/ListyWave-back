package com.listywave.alarm.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("checked")
    private boolean isChecked;
    private LocalDateTime createdDate;
}
