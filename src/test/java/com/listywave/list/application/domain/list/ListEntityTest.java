package com.listywave.list.application.domain.list;

import static com.listywave.common.exception.ErrorCode.DELETED_USER_EXCEPTION;
import static com.listywave.common.exception.ErrorCode.INVALID_ACCESS;
import static com.listywave.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static com.listywave.list.application.domain.category.CategoryType.ANIMAL_PLANT;
import static com.listywave.list.application.domain.category.CategoryType.ENTIRE;
import static com.listywave.list.application.domain.category.CategoryType.FOOD;
import static com.listywave.user.fixture.UserFixture.동호;
import static com.listywave.user.fixture.UserFixture.유진;
import static com.listywave.user.fixture.UserFixture.정수;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.collaborator.application.domain.Collaborator;
import com.listywave.collaborator.application.domain.Collaborators;
import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.category.CategoryType;
import com.listywave.list.application.domain.item.Item;
import com.listywave.list.application.domain.item.ItemComment;
import com.listywave.list.application.domain.item.ItemImageUrl;
import com.listywave.list.application.domain.item.ItemLink;
import com.listywave.list.application.domain.item.ItemTitle;
import com.listywave.list.application.domain.item.Items;
import com.listywave.list.application.domain.label.Label;
import com.listywave.list.application.domain.label.LabelName;
import com.listywave.list.application.domain.label.Labels;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("ListEntity는 ")
class ListEntityTest {

    private final User user = 동호();
    private final CategoryType categoryType = FOOD;
    private final ListTitle title = new ListTitle("칼국수 맛집 TOP 10");
    private final ListDescription description = new ListDescription("박박우기는 칼국수 맛집");
    private final boolean isPublic = true;
    private final BackgroundPalette backGroundPalette = BackgroundPalette.GRAY;
    private final BackgroundColor backgroundColor = BackgroundColor.GRAY_LIGHT;
    private final boolean hasCollaboration = false;
    private final Labels labels = new Labels(List.of(
            Label.init(new LabelName("대전")),
            Label.init(new LabelName("칼국수")),
            Label.init(new LabelName("김치"))
    ));
    private final Items items = new Items(List.of(
            Item.init(1, new ItemTitle("우리집"), new ItemComment("우리집 칼국수가 젤 맛있음"), new ItemLink(""), new ItemImageUrl("")),
            Item.init(2, new ItemTitle("동호집"), new ItemComment("동호집 칼국수가 젤 맛있음ㅋㅋ"), new ItemLink(""), new ItemImageUrl("")),
            Item.init(3, new ItemTitle("정수집"), new ItemComment("정수네가 꼴찌"), new ItemLink(""), new ItemImageUrl("")),
            Item.init(4, new ItemTitle("전주집"), new ItemComment("전주집 맛집임"), new ItemLink(""), new ItemImageUrl("")),
            Item.init(5, new ItemTitle("경주집"), new ItemComment("경주 여행가고 싶다"), new ItemLink(""), new ItemImageUrl(""))
    ));
    private final ListEntity list = new ListEntity(user, categoryType, title, description, isPublic, backGroundPalette, backgroundColor, hasCollaboration, labels, items);

    @Test
    void 아이템을_순위대로_정렬해서_반환한다() {
        Items sortedItems = list.getSortedItems();
        List<String> result = sortedItems.getValues().stream()
                .map(Item::getTitle)
                .map(ItemTitle::getValue)
                .toList();

        List<String> expect = List.of("우리집", "동호집", "정수집", "전주집", "경주집");
        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 아이템을_TOP3까지_가져온다() {
        Items top3Items = list.getTop3Items();
        List<Integer> result = top3Items.getValues().stream()
                .map(Item::getRanking)
                .toList();

        List<Integer> expect = List.of(1, 2, 3);
        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 리스트의_작성자가_동일한지_검증한다() {
        User other = 정수();
        CustomException exception = assertThrows(CustomException.class, () -> list.validateOwner(other));
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_ACCESS);

        assertThatNoException().isThrownBy(() -> list.validateOwner(user));
    }

    @Test
    void 리스트의_작성자가_아닌지_검증한다() {
        CustomException exception = assertThrows(CustomException.class, () -> list.validateNotOwner(user));
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_ACCESS);

        User other = 정수();
        assertThatNoException().isThrownBy(() -> list.validateNotOwner(other));
    }

    @Test
    void 카테고리_타압이_일치하는지_여부를_반환하다() {
        assertThat(list.isCategoryType(ENTIRE)).isTrue();
        assertThat(list.isCategoryType(FOOD)).isTrue();
        assertThat(list.isCategoryType(ANIMAL_PLANT)).isFalse();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "칼국수, true",
            "박박, true",
            "RED, false",
            "대전, true",
            "경주집, true",
            "대구집, false",
            "여행, true",
            "집밥, false"
    })
    void 주어진_문자열이_검색_조건에_일치하는지_여부를_반환한다(String keyword, boolean expect) {
        boolean result = list.isMatch(keyword);

        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 주어진_문자열과_일치한_정도를_점수로_매긴다() {
        String keyword = "칼국수";

        int result = list.scoreRelation(keyword);

        assertThat(result).isEqualTo(5 + 4 + 3 + 1);
    }

    @Test
    void 콜렉트_수를_증가한다() {
        list.increaseCollectCount();

        assertThat(list.getCollectCount()).isOne();
    }

    @Test
    void 콜렉트_수를_감소한다() {
        list.increaseCollectCount();

        list.decreaseCollectCount();

        assertThat(list.getCollectCount()).isZero();
    }

    @Test
    void 콜렉트_수는_음수가_되지_않는다() {
        list.decreaseCollectCount();

        assertThat(list.getCollectCount()).isZero();
    }

    @Test
    void 새로운_Item으로_히스토리를_생성할_수_있는지_여부를_반환한다() {
        Items newItems = new Items(List.of(
                Item.init(1, new ItemTitle("우리집"), new ItemComment("우리집 칼국수가 젤 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("동호집"), new ItemComment("동호집 칼국수가 젤 맛있음ㅋㅋ"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("정수집"), new ItemComment("정수네가 꼴찌"), new ItemLink(""), new ItemImageUrl(""))
        ));

        boolean result = list.canCreateHistory(newItems);

        assertThat(result).isTrue();
    }

    @Test
    void 특정_Item이_있는지_여부를_반환한다() {
        Item contains = Item.init(1, new ItemTitle("우리집"), new ItemComment("우리집 칼국수가 젤 맛있음"), new ItemLink(""), new ItemImageUrl(""));
        assertThatNoException().isThrownBy(() -> list.validateHasItem(contains));

        Item notContains = Item.init(1, new ItemTitle("옆집"), new ItemComment("옆집 칼국수가 젤 맛있음"), new ItemLink(""), new ItemImageUrl(""));
        CustomException exception = assertThrows(CustomException.class, () -> list.validateHasItem(notContains));
        assertThat(exception.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
    }

    @Test
    void 공개_여부를_수정할_수_있다() {
        assertThat(list.isPublic()).isTrue();

        list.updateVisibility();

        assertThat(list.isPublic()).isFalse();
    }

    @Test
    void 대표_이미지_URL을_반환하다() {
        Items items = new Items(List.of(
                Item.init(1, new ItemTitle("우리집"), new ItemComment("우리집 칼국수가 젤 맛있음"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(2, new ItemTitle("동호집"), new ItemComment("동호집 칼국수가 젤 맛있음ㅋㅋ"), new ItemLink(""), new ItemImageUrl("")),
                Item.init(3, new ItemTitle("정수집"), new ItemComment("정수네가 꼴찌"), new ItemLink(""), new ItemImageUrl("https://naver.com"))
        ));
        ListEntity list = new ListEntity(user, categoryType, title, description, isPublic, backGroundPalette, backgroundColor, hasCollaboration, labels, items);

        String result = list.getRepresentImageUrl();

        assertThat(result).isEqualTo("https://naver.com");
    }

    @Test
    void 대표_이미지가_없으면_빈_문자열을_반환한다() {
        assertThat(list.getRepresentImageUrl()).isBlank();
    }

    @Test
    void 작성자가_삭제_처리됐는지_여부를_반환한다() {
        assertThat(list.isDeletedUser()).isFalse();

        user.softDelete();
        assertThat(list.isDeletedUser()).isTrue();
    }

    @Test
    void 작성자가_삭제_처리됐는지_검증한다() {
        assertThatNoException().isThrownBy(list::validateOwnerIsNotDelete);

        user.softDelete();
        CustomException exception = assertThrows(CustomException.class, list::validateOwnerIsNotDelete);
        assertThat(exception.getErrorCode()).isEqualTo(DELETED_USER_EXCEPTION);
    }

    @Test
    void 리스트는_작성자_또는_콜라보레이터에_포함된_유저만이_수정할_수_있다() {
        // given
        User collaboratorUser = 정수();
        Collaborator collaborator = Collaborator.init(collaboratorUser, list);
        User notCollaborator = 유진();

        Collaborators collaborators = new Collaborators(List.of(collaborator));

        // when
        // then
        assertAll(
                () -> assertThatNoException().isThrownBy(() -> list.validateUpdateAuthority(user, collaborators)),
                () -> assertThatNoException().isThrownBy(() -> list.validateUpdateAuthority(collaboratorUser, collaborators)),
                () -> {
                    CustomException exception = assertThrows(CustomException.class, () -> list.validateUpdateAuthority(notCollaborator, collaborators));
                    assertThat(exception.getErrorCode()).isEqualTo(INVALID_ACCESS);
                }
        );
    }
}
