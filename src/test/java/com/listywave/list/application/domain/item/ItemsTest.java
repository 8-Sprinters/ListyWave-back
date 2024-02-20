package com.listywave.list.application.domain.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ItemsTest {

    @Test
    void 아이템_순위에_변동사항이_없으면_히스토리를_생성할_수_없다() {
        // given
        Items items = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl(""))
        ));

        Items newItems = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl(""))
        ));

        // then
        assertThat(items.canCreateHistory(newItems)).isFalse();
    }

    @Test
    void 아이템이_새롭게_변경되면_히스토리를_생성할_수_있다() {
        // given
        Items items = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl(""))
        ));

        Items newItems = new Items(List.of(
                Item.init(1, new ItemTitle("육개장 사발면"), new ItemComment("진리 아님?"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("김치 사발면"), new ItemComment("용호상박"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("신라면"), new ItemComment("신라면은 그래도 빠지지 않지"), new ItemLink(""), new ItemImageUrl(""))
        ));

        // then
        assertThat(items.canCreateHistory(newItems)).isTrue();
    }

    @Test
    void 아이템이_추가되면_히스토리를_생성할_수_있다() {
        // given
        Items items = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl(""))
        ));

        Items newItems = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(4, new ItemTitle("참깨라면"), new ItemComment("피시방 전용 라면"), new ItemLink(""), new ItemImageUrl(""))
        ));

        // then
        assertThat(items.canCreateHistory(newItems)).isTrue();
    }

    @Test
    void 아이템이_삭제되면_히스토리를_생성할_수_있다() {
        // given
        Items beforeItems = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(4, new ItemTitle("참깨라면"), new ItemComment("피시방 전용 라면"), new ItemLink(""), new ItemImageUrl(""))
        ));

        Items newItems = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl(""))
        ));

        // then
        assertThat(beforeItems.canCreateHistory(newItems)).isTrue();
    }

    @Test
    void 아이템_순위에_변경이_생기면_히스토리를_생성할_수_있다() {
        // given
        Items items = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl(""))
        ));

        Items newItems = new Items(List.of(
                Item.init(1, new ItemTitle("간짬뽕"), new ItemComment("간짬뽕이 1위 됐네요"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("신라면"), new ItemComment("신라면이 2위 됐네요"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("진매"), new ItemComment("진매는 3위해라"), new ItemLink(""), new ItemImageUrl(""))
        ));

        // then
        assertThat(items.canCreateHistory(newItems)).isTrue();
    }
}
