package com.listywave.history.application.domain;

import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.정수;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("History는 ")
class HistoryTest {

    private final User user = 동호();
    private final ListEntity list = 가장_좋아하는_견종_TOP3(user, List.of());
    private final List<HistoryItem> historyItems = List.of(
            new HistoryItem(1, new HistoryItemTitle("1등")),
            new HistoryItem(1, new HistoryItemTitle("2등")),
            new HistoryItem(1, new HistoryItemTitle("3등"))
    );

    @Test
    void 히스토리의_주인인지_판별한다() {
        User otherUser = 정수();
        History history = new History(list, historyItems, now(), true);

        assertThatNoException().isThrownBy(() -> history.validateOwner(user));
        CustomException exception = assertThrows(CustomException.class, () -> history.validateOwner(otherUser));
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_ACCESS);
    }

    @Test
    void 공개_여부를_수정할_수_있다() {
        History history = new History(list, historyItems, now(), true);
        history.updatePublic();
        assertThat(history.isPublic()).isFalse();

        history.updatePublic();
        assertThat(history.isPublic()).isTrue();
    }
}
