package com.listywave.list.application.domain.label;

import static com.listywave.common.exception.ErrorCode.INVALID_COUNT;
import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.user.fixture.UserFixture.동호;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.listywave.common.exception.CustomException;
import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.application.domain.list.ListTitle;
import com.listywave.user.application.domain.User;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Labels는 ")
class LabelsTest {

    private final User user = 동호();
    private final ListEntity list = 가장_좋아하는_견종_TOP3(user, List.of());
    private Labels labels = new Labels(List.of(
            Label.init(new LabelName("중식")),
            Label.init(new LabelName("양식"))
    ));

    @Test
    void 최대_제한_개수가_존재한다() {
        List<Label> labels = IntStream.range(0, 4)
                .mapToObj(i -> new LabelName(String.valueOf(i)))
                .map(Label::init)
                .toList();

        CustomException exception = assertThrows(CustomException.class, () -> new Labels(labels));

        assertThat(exception.getErrorCode()).isEqualTo(INVALID_COUNT);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "한식, false",
            "중식, true",
            "양식, true",
            "식, true"
    })
    void 특정_문자열이_Label_하나라도_일치하는지_여부를_반환한다(String keyword, boolean expect) {
        boolean result = labels.anyMatch(keyword);

        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 모든_Label의_list를_수정할_수_있다() {
        labels.updateList(list);

        List<ListTitle> listTitles = labels.getValues().stream()
                .map(Label::getList)
                .map(ListEntity::getTitle)
                .toList();

        assertThat(listTitles).allMatch(listTitle -> listTitle.equals(list.getTitle()));
    }

    @Nested
    class Label을_변경한다 {

        @Test
        void 변경_여부를_반환한다() {
            Labels newLabels = new Labels(List.of(
                    Label.init(new LabelName("중식")),
                    Label.init(new LabelName("양식")),
                    Label.init(new LabelName("한식"))
            ));

            boolean result = labels.isChange(newLabels);

            assertThat(result).isTrue();
        }

        @Test
        void 변경되지_않은_경우_false를_반환한다() {
            Labels newLabels = new Labels(List.of(
                    Label.init(new LabelName("중식")),
                    Label.init(new LabelName("양식"))
            ));

            boolean result = labels.isChange(newLabels);

            assertThat(result).isFalse();
        }

        @Test
        void 새로운_Labels로_아무_값이_들어오지_않으면_모두_삭제된다() {
            Labels newLabels = new Labels(List.of(
            ));

            labels.updateAll(newLabels, list);

            assertThat(labels.getValues()).hasSize(0);
        }

        @Test
        void 새로운_Label이_하나_추가된_경우() {
            Labels newLabels = new Labels(List.of(
                    Label.init(new LabelName("중식")),
                    Label.init(new LabelName("양식")),
                    Label.init(new LabelName("한식"))
            ));

            labels.updateAll(newLabels, list);

            List<String> result = labels.getValues().stream()
                    .map(Label::getName)
                    .toList();
            List<String> expect = List.of("중식", "양식", "한식");
            assertThat(result).isEqualTo(expect);
        }

        @Test
        void Label_하나가_제거된_경우() {
            Labels newLabels = new Labels(List.of(
                    Label.init(new LabelName("중식"))
            ));

            labels.updateAll(newLabels, list);

            List<String> result = labels.getValues().stream()
                    .map(Label::getName)
                    .toList();
            List<String> expect = List.of("중식");
            assertThat(result).isEqualTo(expect);
        }

        @Test
        void Label의_순서가_바뀐_경우() {
            Labels newLabels = new Labels(List.of(
                    Label.init(new LabelName("양식")),
                    Label.init(new LabelName("중식"))
            ));

            labels.updateAll(newLabels, list);

            List<String> result = labels.getValues().stream()
                    .map(Label::getName)
                    .toList();
            List<String> expect = List.of("중식", "양식");
            assertThat(result).isEqualTo(expect);
        }

        @Test
        void 전혀_새로운_Label들로_수정된_경우() {
            Labels newLabels = new Labels(List.of(
                    Label.init(new LabelName("떡볶이")),
                    Label.init(new LabelName("튀김")),
                    Label.init(new LabelName("순대"))
            ));

            labels.updateAll(newLabels, list);

            List<String> result = labels.getValues().stream()
                    .map(Label::getName)
                    .toList();
            List<String> expect = List.of("떡볶이", "튀김", "순대");
            assertThat(result).isEqualTo(expect);
        }
    }
}
