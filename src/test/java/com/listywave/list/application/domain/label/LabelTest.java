package com.listywave.list.application.domain.label;

import static com.listywave.user.fixture.UserFixture.동호;
import static org.assertj.core.api.Assertions.assertThat;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.list.fixture.ListFixture;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Label은 ")
class LabelTest {

    private final User user = 동호();
    private final ListEntity list = ListFixture.가장_좋아하는_견종_TOP3(user, List.of());
    private final LabelName name = new LabelName("강아지");
    private Label label;

    @BeforeEach
    void setUp() {
        label = Label.init(name);
        label.updateList(list);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "강아지, true",
            "개, false",
            "' ', false",
            "아, true",
            "아지, true",
            "지, true",
    })
    void 주어진_문자열이_값에_포함되는지_여부를_반환한다(String keyword, boolean expect) {
        boolean result = label.isMatch(keyword);

        assertThat(result).isEqualTo(expect);
    }

    @Test
    void LabelName값이_같으면_같은_객체이다() {
        LabelName same = new LabelName("강아지");
        LabelName other = new LabelName("고양이");
        Label sameLabel = Label.init(same);
        Label otherLabel = Label.init(other);

        assertThat(label).isEqualTo(sameLabel);
        assertThat(label).hasSameHashCodeAs(sameLabel);
        assertThat(label).isNotEqualTo(otherLabel);
        assertThat(label).doesNotHaveSameHashCodeAs(otherLabel);
    }
}
