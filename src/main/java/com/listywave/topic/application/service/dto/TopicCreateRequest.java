package com.listywave.topic.application.service.dto;

import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.list.ListDescription;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.topic.application.domain.Topic;
import com.listywave.user.application.domain.User;

public record TopicCreateRequest(
        String categoryKorName,
        String title,
        String description,
        boolean isAnonymous
) {

    public Topic toEntity(User user) {
        return Topic.builder()
                .user(user)
                .category(CategoryType.viewNameOf(categoryKorName))
                .title(new ListTitle(title))
                .description(new ListDescription(description))
                .isExposed(true)
                .build();
    }
}
