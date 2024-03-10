package com.listywave.history.application.dto;

import com.listywave.history.application.domain.History;
import com.listywave.history.application.domain.HistoryItem;
import java.time.LocalDateTime;
import java.util.List;

public record HistorySearchResponse(
        Long id,
        LocalDateTime createdDate,
        boolean isPublic,
        List<HistoryItemInfo> items
) {

    public static List<HistorySearchResponse> toList(List<History> histories) {
        return histories.stream()
                .map(HistorySearchResponse::of)
                .toList();
    }

    private static HistorySearchResponse of(History history) {
        return new HistorySearchResponse(
                history.getId(),
                history.getCreatedDate(),
                history.isPublic(),
                HistoryItemInfo.toList(history.getItems())
        );
    }

    public record HistoryItemInfo(
            Long id,
            int rank,
            String title
    ) {

        public static List<HistoryItemInfo> toList(List<HistoryItem> historyItems) {
            return historyItems.stream()
                    .map(HistoryItemInfo::of)
                    .toList();
        }

        private static HistoryItemInfo of(HistoryItem historyItem) {
            return new HistoryItemInfo(historyItem.getId(), historyItem.getRank(), historyItem.getTitle().getValue());
        }
    }
}
