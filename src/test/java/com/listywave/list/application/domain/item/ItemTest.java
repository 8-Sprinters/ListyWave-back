package com.listywave.list.application.domain.item;

import static com.listywave.user.fixture.UserFixture.동호;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.listywave.history.application.domain.HistoryItem;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.fixture.ListFixture;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Item은 ")
class ItemTest {

    private final User user = 동호();
    private final ListEntity list = ListFixture.가장_좋아하는_견종_TOP3(user, List.of());
    private final int rank = 1;
    private final ItemTitle title = new ItemTitle("title");
    private final ItemComment comment = new ItemComment("comment");
    private final ItemLink link = new ItemLink("link");
    private final ItemImageUrl imageUrl = new ItemImageUrl("imageUrl");
    private final Item item = Item.init(rank, title, comment, link, imageUrl);

    @Test
    void ItemImageKey를_수정할_수_있다() {
        String newItemImageKey = "sfksadfhskfhjaf";

        item.updateItemImageKey(newItemImageKey);

        assertThat(item.getImageKey()).isEqualTo(newItemImageKey);
    }

    @Test
    void ItemImageUrl을_수정할_수_있다() {
        String newValue = "sfksadfhskfhjaf";

        item.updateItemImageUrl(newValue);

        assertThat(item.getImageUrl().getValue()).isEqualTo(newValue);
    }

    @Test
    void listEntity를_수정할_수_있다() {
        ListEntity newList = ListFixture.좋아하는_라면_TOP3(user, List.of());

        item.updateList(newList);

        assertThat(item.getList()).isEqualTo(newList);
    }

    @Test
    void HistoryItem_객체를_생성할_수_있다() {
        HistoryItem historyItem = item.toHistoryItem();

        assertAll(
                () -> assertThat(historyItem.getTitle().getValue()).isEqualTo(item.getTitle().getValue()),
                () -> assertThat(historyItem.getRank()).isEqualTo(item.getRanking())
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "title, true",
            "comment, false"
    }, delimiterString = ", ")
    void 주어진_문자열이_Title에_포함되는지_여부를_반환한다(String keyword, boolean expect) {
        boolean result = item.isMatchTitle(keyword);

        assertThat(result).isEqualTo(expect);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "title, false",
            "comment, true"
    }, delimiterString = ", ")
    void 주어진_문자열이_Comment에_포함되는지_여부를_반환한다(String keyword, boolean expect) {
        boolean result = item.isMatchComment(keyword);

        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 순위_제목_설명_사진_URL이_같으면_같은_아이템이다() {
        Item sameItem = Item.init(1, title, comment, link, imageUrl);
        Item differentItem = Item.init(2, title, comment, link, imageUrl);

        assertThat(item).isEqualTo(sameItem);
        assertThat(item).hasSameHashCodeAs(sameItem);
        assertThat(item).isNotEqualTo(differentItem);
        assertThat(item).doesNotHaveSameHashCodeAs(differentItem);
    }
}
