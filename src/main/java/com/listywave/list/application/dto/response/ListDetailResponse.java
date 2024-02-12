package com.listywave.list.application.dto.response;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.list.application.domain.Item;
import com.listywave.list.application.domain.Label;
import com.listywave.list.application.domain.Lists;
import com.listywave.user.application.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ListDetailResponse(
        String category,
        List<LabelResponse> labels,
        String title,
        String description,
        LocalDateTime createdDate,
        LocalDateTime lastUpdatedDate,
        Long ownerId,
        String ownerNickname,
        String ownerProfileImageUrl,
        List<CollaboratorResponse> collaborators,
        List<ItemResponse> items,
        boolean isCollected,
        boolean isPublic,
        String backgroundColor,
        int collectCount,
        int viewCount
) {

    public static ListDetailResponse of(Lists list, User user, boolean isCollected, List<Collaborator> collaborators) {
        return ListDetailResponse.builder()
                .category(list.getCategoryName())
                .labels(LabelResponse.toList(list.getLabels()))
                .title(list.getTitle())
                .description(list.getDescription())
                .createdDate(list.getCreatedDate())
                .lastUpdatedDate(list.getUpdatedDate())
                .ownerId(user.getId())
                .ownerNickname(user.getNickname())
                .ownerProfileImageUrl(user.getProfileImageUrl())
                .collaborators(CollaboratorResponse.toList(collaborators))
                .items(ItemResponse.toList(list.getItems()))
                .isCollected(isCollected)
                .isPublic(list.isPublic())
                .backgroundColor(list.getBackgroundColor())
                .collectCount(list.getCollectCount())
                .viewCount(list.getViewCount())
                .build();
    }
}

record LabelResponse(
        String name
) {

    public static List<LabelResponse> toList(List<Label> labels) {
        return labels.stream()
                .map(LabelResponse::of)
                .toList();
    }

    public static LabelResponse of(Label label) {
        return new LabelResponse(label.getLabelName());
    }
}

@Builder
record CollaboratorResponse(
        Long userId,
        String userNickname,
        String userProfileImageUrl
) {

    public static List<CollaboratorResponse> toList(List<Collaborator> collaborators) {
        return collaborators.stream()
                .map(CollaboratorResponse::of)
                .toList();
    }

    public static CollaboratorResponse of(Collaborator collaborator) {
        return CollaboratorResponse.builder()
                .userId(collaborator.getId())
                .userNickname(collaborator.getUserNickname())
                .userProfileImageUrl(collaborator.getUserProfileImageUrl())
                .build();
    }
}

@Builder
record ItemResponse(
        Long id,
        int rank,
        String title,
        String comment,
        String link,
        String imageUrl
) {

    public static List<ItemResponse> toList(List<Item> items) {
        return items.stream()
                .map(ItemResponse::of)
                .toList();
    }

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .rank(item.getRanking())
                .title(item.getTitle())
                .comment(item.getComment())
                .link(item.getLink())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
