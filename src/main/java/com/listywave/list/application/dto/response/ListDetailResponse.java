package com.listywave.list.application.dto.response;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.list.application.domain.comment.Comment;
import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.label.Label;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ListDetailResponse(
        String categoryEngName,
        String categoryKorName,
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
        String backgroundPalette,
        String backgroundColor,
        int collectCount,
        int viewCount,
        long totalCommentCount,
        @Nullable NewestComment newestComment
) {

    public static ListDetailResponse of(
            ListEntity list,
            User owner,
            boolean isCollected,
            List<Collaborator> collaborators,
            long totalCommentCount,
            Comment newestComment,
            Long totalReplyCount
    ) {
        return ListDetailResponse.builder()
                .categoryEngName(list.getCategory().name().toLowerCase())
                .categoryKorName(list.getCategory().getViewName())
                .labels(LabelResponse.toList(list.getLabels().getValues()))
                .title(list.getTitle().getValue())
                .description(list.getDescription().getValue())
                .createdDate(list.getCreatedDate())
                .lastUpdatedDate(list.getUpdatedDate())
                .ownerId(owner.getId())
                .ownerNickname(owner.getNickname())
                .ownerProfileImageUrl(owner.getProfileImageUrl())
                .collaborators(CollaboratorResponse.toList(collaborators))
                .items(ItemResponse.toList(list.getSortedItems().getValues()))
                .isCollected(isCollected)
                .isPublic(list.isPublic())
                .backgroundColor(list.getBackgroundColor().name())
                .backgroundPalette(list.getBackgroundPalette().name())
                .collectCount(list.getCollectCount())
                .viewCount(list.getViewCount())
                .totalCommentCount(totalCommentCount)
                .newestComment(NewestComment.of(newestComment, totalReplyCount))
                .build();
    }

    public record LabelResponse(
            String name
    ) {

        public static List<LabelResponse> toList(List<Label> labels) {
            return labels.stream()
                    .map(LabelResponse::of)
                    .toList();
        }

        public static LabelResponse of(Label label) {
            return new LabelResponse(label.getName());
        }
    }

    @Builder
    public record CollaboratorResponse(
            Long id,
            String nickname,
            String profileImageUrl
    ) {

        public static List<CollaboratorResponse> toList(List<Collaborator> collaborators) {
            return collaborators.stream()
                    .map(CollaboratorResponse::of)
                    .toList();
        }

        public static CollaboratorResponse of(Collaborator collaborator) {
            return CollaboratorResponse.builder()
                    .id(collaborator.getUser().getId())
                    .nickname(collaborator.getUserNickname())
                    .profileImageUrl(collaborator.getUserProfileImageUrl())
                    .build();
        }
    }

    @Builder
    public record ItemResponse(
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
                    .title(item.getTitle().getValue())
                    .comment(item.getComment().getValue())
                    .link(item.getLink().getValue())
                    .imageUrl(item.getImageUrl().getValue())
                    .build();
        }
    }

    @Builder
    public record NewestComment(
            Long userId,
            String userNickname,
            String userProfileImageUrl,
            LocalDateTime createdDate,
            String content,
            Long totalReplyCount
    ) {

        public static NewestComment of(@Nullable Comment comment, Long totalReplyCount) {
            if (comment == null) {
                return null;
            }
            return NewestComment.builder()
                    .userId(comment.getUserId())
                    .userNickname(comment.getUserNickname())
                    .userProfileImageUrl(comment.getUserProfileImageUrl())
                    .createdDate(comment.getCreatedDate())
                    .content(comment.getCommentContent())
                    .totalReplyCount(totalReplyCount)
                    .build();
        }
    }
}
