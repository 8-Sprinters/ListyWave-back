package com.listywave.collaborator.application.domain;

import static com.listywave.list.fixture.ListFixture.가장_좋아하는_견종_TOP3;
import static com.listywave.user.fixture.UserFixture.동호;
import static org.assertj.core.api.Assertions.assertThat;

import com.listywave.list.application.domain.list.ListEntity;
import com.listywave.user.application.domain.User;
import java.util.List;
import org.junit.jupiter.api.Test;

class CollaboratorTest {

    @Test
    void ID가_달라도_User와_ListEntity값이_같으면_같은_콜라보레이터_객체이다() {
        User user = 동호();
        ListEntity list = 가장_좋아하는_견종_TOP3(user, List.of());

        Collaborator collaborator1 = new Collaborator(1L, user, list);
        Collaborator collaborator2 = new Collaborator(2L, user, list);

        assertThat(collaborator1).isEqualTo(collaborator2);
        assertThat(collaborator1).hasSameHashCodeAs(collaborator2);
    }
}
