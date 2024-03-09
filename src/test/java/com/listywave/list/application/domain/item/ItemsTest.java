package com.listywave.list.application.domain.item;

import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Items는 ")
class ItemsTest {

    private final Items items = new Items(List.of(
            Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
            Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("https://naver.com")),
            Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl(""))
    ));

    @ParameterizedTest
    @ValueSource(ints = {2, 11})
    void 최소_제한_개수와_최대_제한_개수가_존재한다(int size) {
        List<Item> itemValues = IntStream.range(0, size)
                .mapToObj(i -> Item.init(
                        i,
                        new ItemTitle(String.valueOf(i)),
                        new ItemComment(String.valueOf(i)),
                        new ItemLink(String.valueOf(i)),
                        new ItemImageUrl(String.valueOf(i))
                )).toList();

        CustomException exception = assertThrows(CustomException.class, () -> new Items(itemValues));

        assertThat(exception.getErrorCode()).isEqualTo(INVALID_COUNT);
    }

    @Test
    void 아이템_순위대로_정렬한다() {
        Items sorted = items.sortByRank();
        List<Integer> result = sorted.getValues().stream()
                .map(Item::getRanking)
                .collect(Collectors.toList());

        List<Integer> expect = List.of(1, 2, 3);

        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 이미지가_존재하는_아이템_중_가장_높은_순위의_아이템의_이미지를_반환한다() {
        String result = items.getRepresentImageUrl();

        assertThat(result).isEqualTo(items.get(1).getImageUrl().getValue());
    }

    @Test
    void 새로운_Items와_비교해서_변경_여부를_응답한다() {
        Items newItems = new Items(List.of(
                Item.init(1, new ItemTitle("육개장 사발면"), new ItemComment("진리 아님?"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("김치 사발면"), new ItemComment("용호상박"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("신라면"), new ItemComment("신라면은 그래도 빠지지 않지"), new ItemLink(""), new ItemImageUrl(""))
        ));

        boolean result = items.isChange(newItems);

        assertThat(result).isTrue();
        assertThat(items.isChange(items)).isFalse();
    }

    @Test
    void 아이템의_Top3를_응답한다() {
        Items addedItems = new Items(List.of(
                Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("https://naver.com")),
                Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(4, new ItemTitle("참깨라면"), new ItemComment("피시방 전용 라면"), new ItemLink(""), new ItemImageUrl(""))
        ));

        Items result = addedItems.getTop3();

        assertThat(result.getValues()).isEqualTo(items.getValues());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "간짬뽕, true",
            "참깨라면, false"
    })
    void 특정_문자열이_아이템의_Title에_하나라도_포함되는지_여부를_응답한다(String keyword, boolean expect) {
        boolean result = items.anyMatchTitle(keyword);

        assertThat(result).isEqualTo(expect);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "간짬뽕, true",
            "참깨라면, false"
    })
    void 특정_문자열이_아이템의_Comment에_하나라도_포함되는지_여부를_응답한다(String keyword, boolean expect) {
        boolean result = items.anyMatchComment(keyword);

        assertThat(result).isEqualTo(expect);
    }

    @Nested
    class 히스토리를_생성한다 {

        @Test
        void 아이템_순위에_변동사항이_없으면_히스토리를_생성할_수_없다() {
            Items newItems = new Items(List.of(
                    Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl(""))
            ));

            assertThat(items.canCreateHistory(newItems)).isFalse();
        }

        @Test
        void 아이템이_새롭게_변경되면_히스토리를_생성할_수_있다() {
            Items newItems = new Items(List.of(
                    Item.init(1, new ItemTitle("육개장 사발면"), new ItemComment("진리 아님?"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(2, new ItemTitle("김치 사발면"), new ItemComment("용호상박"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(3, new ItemTitle("신라면"), new ItemComment("신라면은 그래도 빠지지 않지"), new ItemLink(""), new ItemImageUrl(""))
            ));

            assertThat(items.canCreateHistory(newItems)).isTrue();
        }

        @Test
        void 아이템이_추가되면_히스토리를_생성할_수_있다() {
            Items newItems = new Items(List.of(
                    Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(4, new ItemTitle("참깨라면"), new ItemComment("피시방 전용 라면"), new ItemLink(""), new ItemImageUrl(""))
            ));

            assertThat(items.canCreateHistory(newItems)).isTrue();
        }

        @Test
        void 아이템이_삭제되면_히스토리를_생성할_수_있다() {
            Items beforeItems = new Items(List.of(
                    Item.init(1, new ItemTitle("신라면"), new ItemComment("사나이 울리는 신라면"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(2, new ItemTitle("진매"), new ItemComment("진매 은근 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(3, new ItemTitle("간짬뽕"), new ItemComment("대학 시절 먹었던 간짬뽕은 1위"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(4, new ItemTitle("참깨라면"), new ItemComment("피시방 전용 라면"), new ItemLink(""), new ItemImageUrl(""))
            ));

            assertThat(beforeItems.canCreateHistory(items)).isTrue();
        }

        @Test
        void 아이템_순위에_변경이_생기면_히스토리를_생성할_수_있다() {
            Items newItems = new Items(List.of(
                    Item.init(1, new ItemTitle("간짬뽕"), new ItemComment("간짬뽕이 1위 됐네요"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(2, new ItemTitle("신라면"), new ItemComment("신라면이 2위 됐네요"), new ItemLink(""), new ItemImageUrl("")),
                    Item.init(3, new ItemTitle("진매"), new ItemComment("진매는 3위해라"), new ItemLink(""), new ItemImageUrl(""))
            ));

            assertThat(items.canCreateHistory(newItems)).isTrue();
        }
    }
}
